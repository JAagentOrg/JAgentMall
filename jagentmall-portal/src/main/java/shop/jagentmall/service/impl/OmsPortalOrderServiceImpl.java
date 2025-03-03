package shop.jagentmall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.PageHelper;
import io.lettuce.core.RedisClient;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import shop.jagentmall.api.CommonPage;
import shop.jagentmall.component.CancelOrderSender;
import shop.jagentmall.constant.RedisKeyPrefixEnum;
import shop.jagentmall.domain.*;
import shop.jagentmall.dao.*;
import shop.jagentmall.exception.Asserts;
import shop.jagentmall.mapper.*;
import shop.jagentmall.model.*;
import shop.jagentmall.service.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Title: OmsPortalOrderServiceImpl
 * @Author: [tianyou]
 * @Date: 2025/2/18
 * @Description: 前台订单管理service实现类
 */
@Service
public class OmsPortalOrderServiceImpl implements OmsPortalOrderService {
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private OmsCartItemService cartItemService;
    @Autowired
    private UmsMemberReceiveAddressService memberReceiveAddressService;
    @Autowired
    private UmsMemberCouponService memberCouponService;
    @Autowired
    private UmsIntegrationConsumeSettingMapper integrationConsumeSettingMapper;
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    @Value("${redis.key.orderId}")
    private String REDIS_KEY_ORDER_ID;
    @Value("${redis.database}")
    private String REDIS_DATABASE;
    @Autowired
    private RedisService redisService;
    @Autowired
    private OmsOrderSettingMapper orderSettingMapper;
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private PortalOrderItemDao orderItemDao;
    @Autowired
    private SmsCouponHistoryMapper couponHistoryMapper;
    @Autowired
    private CancelOrderSender cancelOrderSender;
    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    @Autowired
    private PortalOrderDao portalOrderDao;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private AlipayService alipayService;
    @Override
    public ConfirmOrderResult generateConfirmOrder(List<Long> cartIds) {
        ConfirmOrderResult result = new ConfirmOrderResult();
        // 获取购物车商品，并且带有优惠信息。
        UmsMember currentMember = memberService.getCurrentMember();
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(),cartIds);
        result.setCartPromotionItemList(cartPromotionItemList);
        // 获取用户收货地址列表
        List<UmsMemberReceiveAddress> memberReceiveAddressList = memberReceiveAddressService.list();
        result.setMemberReceiveAddressList(memberReceiveAddressList);
        // 获取用户可用优惠券列表
        List<SmsCouponHistoryDetail> couponHistoryDetailList = memberCouponService.listCart(cartPromotionItemList, 1);
        result.setCouponHistoryDetailList(couponHistoryDetailList);
        // 获取用户积分
        result.setMemberIntegration(currentMember.getIntegration());
        // 获取积分使用规则，目前仅有一条
        UmsIntegrationConsumeSetting integrationConsumeSetting = integrationConsumeSettingMapper.selectByPrimaryKey(1L);
        result.setIntegrationConsumeSetting(integrationConsumeSetting);
        // 计算总金额、活动优惠、应付金额
        CalcAmount calcAmount = calcCartAmount(cartPromotionItemList);
        result.setCalcAmount(calcAmount);

        return result;
    }

    @Override
    public Map<String, Object> generateOrder(OrderParam orderParam) {
        List<OmsOrderItem> orderItemList = new ArrayList<>();
        if(orderParam.getMemberReceiveAddressId() == null){
            Asserts.fail("请选择收货地址！");
        }
        // 获取购物车及优惠信息
        UmsMember currentMember = memberService.getCurrentMember();
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(), orderParam.getCartIds());
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            // 生成下单商品信息
            OmsOrderItem orderItem = new OmsOrderItem();
            orderItem.setProductId(cartPromotionItem.getProductId());
            orderItem.setProductName(cartPromotionItem.getProductName());
            orderItem.setProductPic(cartPromotionItem.getProductPic());
            orderItem.setProductAttr(cartPromotionItem.getProductAttr());
            orderItem.setProductBrand(cartPromotionItem.getProductBrand());
            orderItem.setProductSn(cartPromotionItem.getProductSn());
            orderItem.setProductPrice(cartPromotionItem.getPrice());
            orderItem.setProductQuantity(cartPromotionItem.getQuantity());
            orderItem.setProductSkuId(cartPromotionItem.getProductSkuId());
            orderItem.setProductSkuCode(cartPromotionItem.getProductSkuCode());
            orderItem.setProductCategoryId(cartPromotionItem.getProductCategoryId());
            orderItem.setPromotionAmount(cartPromotionItem.getReduceAmount());
            orderItem.setPromotionName(cartPromotionItem.getPromotionMessage());
            orderItem.setGiftIntegration(cartPromotionItem.getIntegration());
            orderItem.setGiftGrowth(cartPromotionItem.getGrowth());
            orderItemList.add(orderItem);
        }
        // 判断购物车中商品是否都有库存
        if (!hasStock(cartPromotionItemList)) {
            Asserts.fail("库存不足，无法下单");
        }
        // 判断使用使用了优惠券
        if (orderParam.getCouponId() == null) {
            // 不用优惠券
            for (OmsOrderItem orderItem : orderItemList) {
                orderItem.setCouponAmount(new BigDecimal(0));
            }
        } else {
            // 使用优惠券
            SmsCouponHistoryDetail couponHistoryDetail = getUseCoupon(cartPromotionItemList, orderParam.getCouponId());
            if (couponHistoryDetail == null) {
                Asserts.fail("该优惠券不可用");
            }
            // 对下单商品的优惠券进行处理,对优惠券金额分配到每一个商品头上
            handleCouponAmount(orderItemList, couponHistoryDetail);
        }
        //判断是否使用积分
        if (orderParam.getUseIntegration() == null||orderParam.getUseIntegration().equals(0)) {
            // 不使用积分
            for (OmsOrderItem orderItem : orderItemList) {
                orderItem.setIntegrationAmount(new BigDecimal(0));
            }
        } else {
            // 使用积分
            BigDecimal totalAmount = calcTotalAmount(orderItemList);
            BigDecimal integrationAmount = getUseIntegrationAmount(orderParam.getUseIntegration(), totalAmount, currentMember, orderParam.getCouponId() != null);
            if (integrationAmount.compareTo(new BigDecimal(0)) == 0) {
                Asserts.fail("积分不可用");
            } else {
                //可用情况下分摊到可用商品中
                for (OmsOrderItem orderItem : orderItemList) {
                    BigDecimal perAmount = orderItem.getProductPrice().divide(totalAmount, 3, RoundingMode.HALF_EVEN).multiply(integrationAmount);
                    orderItem.setIntegrationAmount(perAmount);
                }
            }
        }
        // 计算order_item的实付金额
        handleRealAmount(orderItemList);
        // 进行库存锁定
        lockStock(cartPromotionItemList);
        // TODO 对库存锁定进行升级，使用缓存+消息队列

        //根据商品合计、运费、活动优惠、优惠券、积分计算应付金额
        OmsOrder order = new OmsOrder();
        order.setDiscountAmount(new BigDecimal(0));
        order.setTotalAmount(calcTotalAmount(orderItemList));
        order.setFreightAmount(new BigDecimal(0));
        order.setPromotionAmount(calcPromotionAmount(orderItemList));
        order.setPromotionInfo(getOrderPromotionInfo(orderItemList));
        if (orderParam.getCouponId() == null) {
            order.setCouponAmount(new BigDecimal(0));
        }
        else {
            order.setCouponId(orderParam.getCouponId());
            order.setCouponAmount(calcCouponAmount(orderItemList));
        }
        if (orderParam.getUseIntegration() == null) {
            order.setIntegration(0);
            order.setIntegrationAmount(new BigDecimal(0));
        }
        else {
            order.setIntegration(orderParam.getUseIntegration());
            order.setIntegrationAmount(calcIntegrationAmount(orderItemList));
        }
        order.setPayAmount(calcPayAmount(order));
        // 转化为订单信息并插入数据库
        order.setMemberId(currentMember.getId());
        order.setCreateTime(new Date());
        order.setMemberUsername(currentMember.getUsername());
        // 支付方式：0->未支付；1->支付宝；2->微信
        order.setPayType(orderParam.getPayType());
        // 订单来源：0->PC订单；1->app订单
        order.setSourceType(1);
        // 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
        order.setStatus(0);
        // 订单类型：0->正常订单；1->秒杀订单
        order.setOrderType(0);
        // 收货人信息：姓名、电话、邮编、地址
        UmsMemberReceiveAddress address = memberReceiveAddressService.getItem(orderParam.getMemberReceiveAddressId());
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverPostCode(address.getPostCode());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverDetailAddress(address.getDetailAddress());
        // 0->未确认；1->已确认
        order.setConfirmStatus(0);
        order.setDeleteStatus(0);
        // 计算赠送积分
        order.setIntegration(calcGifIntegration(orderItemList));
        // 计算赠送成长值
        order.setGrowth(calcGiftGrowth(orderItemList));
        // 生成订单号
        order.setOrderSn(generateOrderSn(order));
        // 设置自动收货天数
        List<OmsOrderSetting> orderSettings = orderSettingMapper.selectByExample(new OmsOrderSettingExample());
        if(CollUtil.isNotEmpty(orderSettings)){
            order.setAutoConfirmDay(orderSettings.get(0).getConfirmOvertime());
        }
        // 插入订单主表
        orderMapper.insert(order);
        for (OmsOrderItem orderItem : orderItemList) {
            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(order.getOrderSn());
        }
        // 插入订单子表
        orderItemDao.insertList(orderItemList);
        // 如使用优惠券更新优惠券使用状态
        if (orderParam.getCouponId() != null) {
            updateCouponStatus(orderParam.getCouponId(), currentMember.getId(), 1);
        }
        // 如使用积分需要扣除积分
        if (orderParam.getUseIntegration() != null) {
            order.setUseIntegration(orderParam.getUseIntegration());
            if(currentMember.getIntegration()==null){
                currentMember.setIntegration(0);
            }
            memberService.updateIntegration(currentMember.getId(), currentMember.getIntegration() - orderParam.getUseIntegration());
        }
        // 删除购物车中的下单商品
        deleteCartItemList(cartPromotionItemList, currentMember);
        // 发送延迟消息到消息队列
        sendDelayMessageCancelOrder(order.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("orderItemList", orderItemList);
        return result;
    }

    @Override
    public Map<String, Object> generateOrderV1(OrderParamV1 orderParam) {
        List<OmsOrderItem> orderItemList = new ArrayList<>();
        if(orderParam.getMemberReceiveAddressId() == null){
            Asserts.fail("请选择收货地址！");
        }
        // 判断购物车中商品是否都有库存,如果充足则锁定库存
        if (!hasStockAndLockedByCache(orderParam)) {
            Asserts.fail("库存不足，无法下单");
        }
        List<Long> cartIds = orderParam.getCartInfo().stream().map(item -> item.getCartId()).collect(Collectors.toList());
        // 获取购物车及优惠信息
        UmsMember currentMember = memberService.getCurrentMember();
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(), cartIds);
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            // 生成下单商品信息
            OmsOrderItem orderItem = new OmsOrderItem();
            orderItem.setProductId(cartPromotionItem.getProductId());
            orderItem.setProductName(cartPromotionItem.getProductName());
            orderItem.setProductPic(cartPromotionItem.getProductPic());
            orderItem.setProductAttr(cartPromotionItem.getProductAttr());
            orderItem.setProductBrand(cartPromotionItem.getProductBrand());
            orderItem.setProductSn(cartPromotionItem.getProductSn());
            orderItem.setProductPrice(cartPromotionItem.getPrice());
            orderItem.setProductQuantity(cartPromotionItem.getQuantity());
            orderItem.setProductSkuId(cartPromotionItem.getProductSkuId());
            orderItem.setProductSkuCode(cartPromotionItem.getProductSkuCode());
            orderItem.setProductCategoryId(cartPromotionItem.getProductCategoryId());
            orderItem.setPromotionAmount(cartPromotionItem.getReduceAmount());
            orderItem.setPromotionName(cartPromotionItem.getPromotionMessage());
            orderItem.setGiftIntegration(cartPromotionItem.getIntegration());
            orderItem.setGiftGrowth(cartPromotionItem.getGrowth());
            orderItemList.add(orderItem);
        }
        // 判断使用使用了优惠券
        if (orderParam.getCouponId() == null) {
            // 不用优惠券
            for (OmsOrderItem orderItem : orderItemList) {
                orderItem.setCouponAmount(new BigDecimal(0));
            }
        } else {
            // 使用优惠券
            SmsCouponHistoryDetail couponHistoryDetail = getUseCoupon(cartPromotionItemList, orderParam.getCouponId());
            if (couponHistoryDetail == null) {
                Asserts.fail("该优惠券不可用");
            }
            // 对下单商品的优惠券进行处理,对优惠券金额分配到每一个商品头上
            handleCouponAmount(orderItemList, couponHistoryDetail);
        }
        //判断是否使用积分
        if (orderParam.getUseIntegration() == null||orderParam.getUseIntegration().equals(0)) {
            // 不使用积分
            for (OmsOrderItem orderItem : orderItemList) {
                orderItem.setIntegrationAmount(new BigDecimal(0));
            }
        } else {
            // 使用积分
            BigDecimal totalAmount = calcTotalAmount(orderItemList);
            BigDecimal integrationAmount = getUseIntegrationAmount(orderParam.getUseIntegration(), totalAmount, currentMember, orderParam.getCouponId() != null);
            if (integrationAmount.compareTo(new BigDecimal(0)) == 0) {
                Asserts.fail("积分不可用");
            } else {
                //可用情况下分摊到可用商品中
                for (OmsOrderItem orderItem : orderItemList) {
                    BigDecimal perAmount = orderItem.getProductPrice().divide(totalAmount, 3, RoundingMode.HALF_EVEN).multiply(integrationAmount);
                    orderItem.setIntegrationAmount(perAmount);
                }
            }
        }
        // 计算order_item的实付金额
        handleRealAmount(orderItemList);
        // 进行库存锁定
        lockStock(cartPromotionItemList);
        //根据商品合计、运费、活动优惠、优惠券、积分计算应付金额
        OmsOrder order = new OmsOrder();
        order.setDiscountAmount(new BigDecimal(0));
        order.setTotalAmount(calcTotalAmount(orderItemList));
        order.setFreightAmount(new BigDecimal(0));
        order.setPromotionAmount(calcPromotionAmount(orderItemList));
        order.setPromotionInfo(getOrderPromotionInfo(orderItemList));
        if (orderParam.getCouponId() == null) {
            order.setCouponAmount(new BigDecimal(0));
        }
        else {
            order.setCouponId(orderParam.getCouponId());
            order.setCouponAmount(calcCouponAmount(orderItemList));
        }
        if (orderParam.getUseIntegration() == null) {
            order.setIntegration(0);
            order.setIntegrationAmount(new BigDecimal(0));
        }
        else {
            order.setIntegration(orderParam.getUseIntegration());
            order.setIntegrationAmount(calcIntegrationAmount(orderItemList));
        }
        order.setPayAmount(calcPayAmount(order));
        // 转化为订单信息并插入数据库
        order.setMemberId(currentMember.getId());
        order.setCreateTime(new Date());
        order.setMemberUsername(currentMember.getUsername());
        // 支付方式：0->未支付；1->支付宝；2->微信
        order.setPayType(orderParam.getPayType());
        // 订单来源：0->PC订单；1->app订单
        order.setSourceType(1);
        // 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
        order.setStatus(0);
        // 订单类型：0->正常订单；1->秒杀订单
        order.setOrderType(0);
        // 收货人信息：姓名、电话、邮编、地址
        UmsMemberReceiveAddress address = memberReceiveAddressService.getItem(orderParam.getMemberReceiveAddressId());
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverPostCode(address.getPostCode());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverDetailAddress(address.getDetailAddress());
        // 0->未确认；1->已确认
        order.setConfirmStatus(0);
        order.setDeleteStatus(0);
        // 计算赠送积分
        order.setIntegration(calcGifIntegration(orderItemList));
        // 计算赠送成长值
        order.setGrowth(calcGiftGrowth(orderItemList));
        // 生成订单号
        order.setOrderSn(generateOrderSn(order));
        // 设置自动收货天数
        List<OmsOrderSetting> orderSettings = orderSettingMapper.selectByExample(new OmsOrderSettingExample());
        if(CollUtil.isNotEmpty(orderSettings)){
            order.setAutoConfirmDay(orderSettings.get(0).getConfirmOvertime());
        }
        // 插入订单主表
        orderMapper.insert(order);
        for (OmsOrderItem orderItem : orderItemList) {
            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(order.getOrderSn());
        }
        // 插入订单子表
        orderItemDao.insertList(orderItemList);
        // 如使用优惠券更新优惠券使用状态
        if (orderParam.getCouponId() != null) {
            updateCouponStatus(orderParam.getCouponId(), currentMember.getId(), 1);
        }
        // 如使用积分需要扣除积分
        if (orderParam.getUseIntegration() != null) {
            order.setUseIntegration(orderParam.getUseIntegration());
            if(currentMember.getIntegration() == null){
                currentMember.setIntegration(0);
            }
            memberService.updateIntegration(currentMember.getId(), currentMember.getIntegration() - orderParam.getUseIntegration());
        }
        // 删除购物车中的下单商品
        deleteCartItemList(cartPromotionItemList, currentMember);
        // 发送延迟消息到消息队列
        sendDelayMessageCancelOrder(order.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("orderItemList", orderItemList);
        return result;
    }

    /**
     * redis判断是否有充足的库存
     * @param orderParam
     * @return
     */
    private boolean hasStockAndLockedByCache(OrderParamV1 orderParam) {
        boolean flag = true;
        List<OrderParamV1.CartInfo> cartInfoList = orderParam.getCartInfo();
        // 先竞争到本地锁，再竞争分布式锁
        synchronized (OmsPortalOrderService.class){
            // 竞争分布式锁
            RLock rLock = redissonClient.getLock(RedisKeyPrefixEnum.LOCK_STOCK.getPrefixKey());
            rLock.lock();
            try{
                Map<String,Integer> updateStockMap = new HashMap<>();
                for(OrderParamV1.CartInfo cartInfo: cartInfoList){
                    Long productId = cartInfo.getProductId();
                    Long productSkuId = cartInfo.getProductSkuId();
                    Integer quantity = cartInfo.getQuantity();
                    String key = RedisKeyPrefixEnum.ORDER_CHECK_STOCK.getPrefixKey() + String.valueOf(productId) + String.valueOf(productSkuId);
                    String stock = stringRedisTemplate.opsForValue().get(key);
                    Integer nowStock;
                    if(stock == null){
                        // 如果redis中没有获取到,则从数据库中查找,并存入到redis中
                        nowStock = addStockToRedis(productId,productSkuId);
                    }
                    else{
                        nowStock = Integer.valueOf(stock);
                    }
                    if(nowStock == null || nowStock.compareTo(quantity) < 0){
                        // 如果库存不满足,则返回
                        flag = false;
                        break;
                    }
                    else{
                        Integer updateStock = nowStock - quantity;
                        updateStockMap.put(key,updateStock);
                    }
                }
                // 如果都满足库存，则去锁定这些库存数量
                if(flag){
                    for(Map.Entry<String,Integer> entry: updateStockMap.entrySet()){
                        String key = entry.getKey();
                        String val = String.valueOf(entry.getValue());
                        stringRedisTemplate.opsForValue().set(key,val,10,TimeUnit.MINUTES);
                    }
                }
            }finally {
                rLock.unlock();
            }
        }
        return flag;
    }

    /**
     * 将sku对应的商品库存添加到redis中
     * @param productId
     * @param productSkuId
     * @return
     */
    private Integer addStockToRedis(Long productId, Long productSkuId){
        PmsSkuStockExample skuStockExample = new PmsSkuStockExample();
        skuStockExample.createCriteria().andIdEqualTo(productSkuId);
        List<PmsSkuStock> skuStockList = skuStockMapper.selectByExample(skuStockExample);
        if(skuStockList.size() > 0){
            PmsSkuStock skuStock = skuStockList.get(0);
            String key = RedisKeyPrefixEnum.ORDER_CHECK_STOCK.getPrefixKey() + String.valueOf(productId) + String.valueOf(productSkuId);
            String val = Integer.toString(skuStock.getStock() - skuStock.getLockStock());
            // 缓存信息保存10分钟
            stringRedisTemplate.opsForValue().set(key, val,10, TimeUnit.MINUTES);
            return skuStock.getStock() - skuStock.getLockStock();
        }
        else{
            return null;
        }
    }

    @Override
    public void sendDelayMessageCancelOrder(Long orderId) {
        //获取订单超时时间
        OmsOrderSetting orderSetting = orderSettingMapper.selectByPrimaryKey(1L);
        long delayTimes = orderSetting.getNormalOrderOvertime() * 60 * 1000;
        //发送延迟消息
        cancelOrderSender.sendMessage(orderId, delayTimes);
    }

    @Override
    public void cancelOrder(Long orderId) {
        //查询未付款的取消订单
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andIdEqualTo(orderId).andStatusEqualTo(0).andDeleteStatusEqualTo(0);
        List<OmsOrder> cancelOrderList = orderMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(cancelOrderList)) {
            return;
        }
        OmsOrder cancelOrder = cancelOrderList.get(0);
        if (cancelOrder != null) {
            alipayService.colsePay(cancelOrder.getOrderSn());
            //修改订单状态为取消
            cancelOrder.setStatus(4);
            orderMapper.updateByPrimaryKeySelective(cancelOrder);
            OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
            orderItemExample.createCriteria().andOrderIdEqualTo(orderId);
            List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
            //解除订单商品库存锁定
            if (!CollectionUtils.isEmpty(orderItemList)) {
                portalOrderDao.releaseSkuStockLock(orderItemList);
            }
            //修改优惠券使用状态
            updateCouponStatus(cancelOrder.getCouponId(), cancelOrder.getMemberId(), 0);
            //返还使用积分
            if (cancelOrder.getUseIntegration() != null) {
                UmsMember member = memberService.getById(cancelOrder.getMemberId());
                memberService.updateIntegration(cancelOrder.getMemberId(), member.getIntegration() + cancelOrder.getUseIntegration());
            }
            // 恢复redis中的库存
            callBackStock(cancelOrder.getOrderSn());
        }
    }

    @Override
    public CommonPage<OmsOrderDetail> list(Integer status, Integer pageNum, Integer pageSize) {
        // status为1，是要获取所有的订单
        if(status == -1){
            status = null;
        }
        UmsMember member = memberService.getCurrentMember();
        PageHelper.startPage(pageNum,pageSize);
        // 首先获取订单主表信息
        OmsOrderExample orderExample = new OmsOrderExample();
        OmsOrderExample.Criteria criteria = orderExample.createCriteria();
        criteria.andDeleteStatusEqualTo(0)
                .andMemberIdEqualTo(member.getId());
        if(status != null){
            criteria.andStatusEqualTo(status);
        }
        orderExample.setOrderByClause("create_time desc");
        List<OmsOrder> orderList = orderMapper.selectByExample(orderExample);
        // 将分页信息存储到orderPage中
        CommonPage<OmsOrder> orderPage = CommonPage.restPage(orderList);
        // 设置分页信息
        CommonPage<OmsOrderDetail> resultPage = new CommonPage<>();
        resultPage.setPageNum(orderPage.getPageNum());
        resultPage.setPageSize(orderPage.getPageSize());
        resultPage.setTotal(orderPage.getTotal());
        resultPage.setTotalPage(orderPage.getTotalPage());
        if(CollUtil.isEmpty(orderList)){
            return resultPage;
        }
        // 设置数据信息
        List<Long> orderIds = orderList.stream().map(OmsOrder::getId).collect(Collectors.toList());
        OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
        orderItemExample.createCriteria().andOrderIdIn(orderIds);
        List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
        List<OmsOrderDetail> orderDetailList = new ArrayList<>();
        for (OmsOrder omsOrder : orderList) {
            OmsOrderDetail orderDetail = new OmsOrderDetail();
            BeanUtil.copyProperties(omsOrder,orderDetail);
            List<OmsOrderItem> relatedItemList = orderItemList.stream().filter(item -> item.getOrderId().equals(orderDetail.getId())).collect(Collectors.toList());
            orderDetail.setOrderItemList(relatedItemList);
            orderDetailList.add(orderDetail);
        }
        resultPage.setList(orderDetailList);
        return resultPage;
    }

    @Override
    public OmsOrderDetail detail(Long orderId) {
        OmsOrder omsOrder = orderMapper.selectByPrimaryKey(orderId);
        OmsOrderItemExample example = new OmsOrderItemExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(example);
        OmsOrderDetail orderDetail = new OmsOrderDetail();
        BeanUtil.copyProperties(omsOrder,orderDetail);
        orderDetail.setOrderItemList(orderItemList);
        return orderDetail;
    }

    @Override
    public void confirmReceiveOrder(Long orderId) {
        UmsMember member = memberService.getCurrentMember();
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if(!member.getId().equals(order.getMemberId())){
            Asserts.fail("不能确认他人订单！");
        }
        if(order.getStatus()!=2){
            Asserts.fail("该订单还未发货！");
        }
        order.setStatus(3);
        order.setConfirmStatus(1);
        order.setReceiveTime(new Date());
        orderMapper.updateByPrimaryKey(order);
    }

    @Override
    public void deleteOrder(Long orderId) {
        UmsMember member = memberService.getCurrentMember();
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if(!member.getId().equals(order.getMemberId())){
            Asserts.fail("不能删除他人订单！");
        }
        if(order.getStatus()==3||order.getStatus()==4){
            order.setDeleteStatus(1);
            orderMapper.updateByPrimaryKey(order);
        }else{
            Asserts.fail("只能删除已完成或已关闭的订单！");
        }
    }

    @Override
    public void paySuccessByOrderSn(String orderSn, Integer payType) {
        OmsOrderExample example =  new OmsOrderExample();
        example.createCriteria()
                .andOrderSnEqualTo(orderSn)
                .andStatusEqualTo(0)
                .andDeleteStatusEqualTo(0);
        List<OmsOrder> orderList = orderMapper.selectByExample(example);
        if(CollUtil.isNotEmpty(orderList)){
            OmsOrder order = orderList.get(0);
            paySuccess(order.getId(),payType);
        }
    }

    @Override
    public Integer paySuccess(Long orderId, Integer payType) {
        //修改订单支付状态
        OmsOrder order = new OmsOrder();
        order.setId(orderId);
        order.setStatus(1);
        order.setPaymentTime(new Date());
        order.setPayType(payType);
        orderMapper.updateByPrimaryKeySelective(order);
        //恢复所有下单商品的锁定库存，扣减真实库存
        OmsOrderDetail orderDetail = portalOrderDao.getDetail(orderId);
        int count = portalOrderDao.updateSkuStock(orderDetail.getOrderItemList());
        return count;
    }

    @Override
    public void callBackStock(String orderSn) {
        OmsOrderItemExample example = new OmsOrderItemExample();
        example.createCriteria().andOrderSnEqualTo(orderSn);
        List<OmsOrderItem> itemList = orderItemMapper.selectByExample(example);
        synchronized (OmsPortalOrderService.class){
            RLock rLock = redissonClient.getLock(RedisKeyPrefixEnum.LOCK_STOCK.getPrefixKey());
            rLock.lock();
            try {
                for(OmsOrderItem item: itemList){
                    Long productId = item.getProductId();
                    Long productSkuId = item.getProductSkuId();
                    Integer quantity = item.getProductQuantity();
                    String key = RedisKeyPrefixEnum.ORDER_CHECK_STOCK.getPrefixKey() + String.valueOf(productId) + String.valueOf(productSkuId);
                    String stock = stringRedisTemplate.opsForValue().get(key);
                    Integer nowStock;
                    if(stock == null){
                        // 如果redis中没有获取到,则从数据库中查找,并存入到redis中
                        addStockToRedis(productId,productSkuId);
                    }
                    else{
                        nowStock = Integer.valueOf(stock);
                        Integer updateStock = nowStock + quantity;
                        String updateStockStr = String.valueOf(updateStock);
                        stringRedisTemplate.opsForValue().set(key,updateStockStr,10,TimeUnit.MINUTES);
                    }
                }
            }finally {
                rLock.unlock();
            }
        }
    }

    @Override
    public Boolean updateOrderStatus(String orderSn, Integer status) {
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andOrderSnEqualTo(orderSn);
        OmsOrder order = new OmsOrder();
        order.setStatus(status);
        int cnt = orderMapper.updateByExampleSelective(order,example);
        return cnt > 0;
    }

    @Override
    public OmsOrder getOrder(Long orderId) {
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        return order;
    }


    /**
     * 计算购物车中总金额，包含原价，优惠金额，实际支付金额
     * @param cartPromotionItemList
     * @return
     */
    private CalcAmount calcCartAmount(List<CartPromotionItem> cartPromotionItemList) {
        CalcAmount calcAmount = new CalcAmount();
        calcAmount.setFreightAmount(new BigDecimal(0));
        BigDecimal totalAmount = new BigDecimal("0");
        BigDecimal promotionAmount = new BigDecimal("0");
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            totalAmount = totalAmount.add(cartPromotionItem.getPrice().multiply(new BigDecimal(cartPromotionItem.getQuantity())));
            promotionAmount = promotionAmount.add(cartPromotionItem.getReduceAmount().multiply(new BigDecimal(cartPromotionItem.getQuantity())));
        }
        calcAmount.setTotalAmount(totalAmount);
        calcAmount.setPromotionAmount(promotionAmount);
        calcAmount.setPayAmount(totalAmount.subtract(promotionAmount));
        return calcAmount;
    }

    /**
     * 判断商品是否都有库存
     * @param cartPromotionItemList
     * @return
     */
    private boolean hasStock(List<CartPromotionItem> cartPromotionItemList) {
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            if (cartPromotionItem.getRealStock() == null //判断真实库存是否为空
                    || cartPromotionItem.getRealStock() <= 0 //判断真实库存是否小于0
                    || cartPromotionItem.getRealStock() < cartPromotionItem.getQuantity()) //判断真实库存是否小于下单的数量
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取能够使用的优惠券
     * @param cartPromotionItemList
     * @param couponId
     * @return
     */
    private SmsCouponHistoryDetail getUseCoupon(List<CartPromotionItem> cartPromotionItemList, Long couponId) {
        List<SmsCouponHistoryDetail> couponHistoryDetailList = memberCouponService.listCart(cartPromotionItemList, 1);
        for (SmsCouponHistoryDetail couponHistoryDetail : couponHistoryDetailList) {
            if (couponHistoryDetail.getCoupon().getId().equals(couponId)) {
                return couponHistoryDetail;
            }
        }
        return null;
    }

    /**
     * 处理优惠券对商品的优惠金额分配
     * @param orderItemList
     * @param couponHistoryDetail
     */
    private void handleCouponAmount(List<OmsOrderItem> orderItemList, SmsCouponHistoryDetail couponHistoryDetail) {
        SmsCoupon coupon = couponHistoryDetail.getCoupon();
        if (coupon.getUseType().equals(0)) {
            //全场通用
            calcPerCouponAmount(orderItemList, coupon);
        } else if (coupon.getUseType().equals(1)) {
            //指定分类
            List<OmsOrderItem> couponOrderItemList = getCouponOrderItemByCategoryRelation(couponHistoryDetail, orderItemList);
            calcPerCouponAmount(couponOrderItemList, coupon);
        } else if (coupon.getUseType().equals(2)) {
            //指定商品
            List<OmsOrderItem> couponOrderItemList = getCouponOrderItemByProductRelation(couponHistoryDetail, orderItemList);
            calcPerCouponAmount(couponOrderItemList, coupon);
        }
    }



    /**
     * 将优惠的金额平均分配到每一个商品
     * @param orderItemList
     * @param coupon
     */
    private void calcPerCouponAmount(List<OmsOrderItem> orderItemList, SmsCoupon coupon) {
        BigDecimal totalAmount = calcTotalAmount(orderItemList);
        for (OmsOrderItem orderItem : orderItemList) {
            // (商品价格/可用商品总价)*优惠券面额
            BigDecimal couponAmount = orderItem.getProductPrice().divide(totalAmount, 3, RoundingMode.HALF_EVEN).multiply(coupon.getAmount());
            orderItem.setCouponAmount(couponAmount);
        }
    }

    /**
     * 计算订单商品中的总金额
     * @param orderItemList
     * @return
     */
    private BigDecimal calcTotalAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsOrderItem item : orderItemList) {
            totalAmount = totalAmount.add(item.getProductPrice().multiply(new BigDecimal(item.getProductQuantity())));
        }
        return totalAmount;
    }

    /**
     * 使用的是指定类的商品可用的优惠券，获取与优惠券有关的订单商品
     * @param couponHistoryDetail
     * @param orderItemList
     * @return
     */
    private List<OmsOrderItem> getCouponOrderItemByCategoryRelation(SmsCouponHistoryDetail couponHistoryDetail, List<OmsOrderItem> orderItemList) {
        List<SmsCouponProductCategoryRelation> categoryRelationList = couponHistoryDetail.getCategoryRelationList();
        List<Long> productCategoryIdList = categoryRelationList.stream().map(SmsCouponProductCategoryRelation::getProductCategoryId).toList();
        List<OmsOrderItem> omsOrderItemList = new ArrayList<>();
        for(OmsOrderItem item: orderItemList){
            if(productCategoryIdList.contains(item.getProductCategoryId())){
                omsOrderItemList.add(item);
            }
            else{
                item.setCouponAmount(new BigDecimal(0));
            }
        }
        return omsOrderItemList;
    }

    /**
     * 使用的是指定商品可用的优惠券，获取与优惠券有关的商品
     * @param couponHistoryDetail
     * @param orderItemList
     * @return
     */
    private List<OmsOrderItem> getCouponOrderItemByProductRelation(SmsCouponHistoryDetail couponHistoryDetail, List<OmsOrderItem> orderItemList) {
        List<SmsCouponProductRelation> productRelationList = couponHistoryDetail.getProductRelationList();
        List<Long> productIdList = productRelationList.stream().map(SmsCouponProductRelation::getProductId).toList();
        List<OmsOrderItem> omsOrderItemList = new ArrayList<>();
        for(OmsOrderItem item: orderItemList){
            if(productIdList.contains(item.getProductCategoryId())){
                omsOrderItemList.add(item);
            }
            else{
                item.setCouponAmount(new BigDecimal(0));
            }
        }
        return omsOrderItemList;
    }

    /**
     * 获取用户能够使用的积分
     * @param useIntegration
     * @param totalAmount
     * @param currentMember
     * @param hasCoupon
     * @return
     */
    private BigDecimal getUseIntegrationAmount(Integer useIntegration, BigDecimal totalAmount, UmsMember currentMember, boolean hasCoupon) {
        BigDecimal zeroAmount = new BigDecimal(0);
        //判断用户是否有这么多积分
        if (useIntegration.compareTo(currentMember.getIntegration()) > 0) {
            return zeroAmount;
        }
        //根据积分使用规则判断是否可用
        //是否可与优惠券共用
        UmsIntegrationConsumeSetting integrationConsumeSetting = integrationConsumeSettingMapper.selectByPrimaryKey(1L);
        if (hasCoupon && integrationConsumeSetting.getCouponStatus().equals(0)) {
            //不可与优惠券共用
            return zeroAmount;
        }
        //是否达到最低使用积分门槛
        if (useIntegration.compareTo(integrationConsumeSetting.getUseUnit()) < 0) {
            return zeroAmount;
        }
        //是否超过订单抵用最高百分比
        BigDecimal integrationAmount = new BigDecimal(useIntegration).divide(new BigDecimal(integrationConsumeSetting.getUseUnit()), 2, RoundingMode.HALF_EVEN);
        BigDecimal maxPercent = new BigDecimal(integrationConsumeSetting.getMaxPercentPerOrder()).divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN);
        if (integrationAmount.compareTo(totalAmount.multiply(maxPercent)) > 0) {
            return zeroAmount;
        }
        return integrationAmount;
    }

    /**
     * 获取订单的实际金额
     * @param orderItemList
     */
    private void handleRealAmount(List<OmsOrderItem> orderItemList) {
        for (OmsOrderItem orderItem : orderItemList) {
            //原价-促销优惠-优惠券抵扣-积分抵扣
            BigDecimal realAmount = orderItem.getProductPrice()
                    .subtract(orderItem.getPromotionAmount())
                    .subtract(orderItem.getCouponAmount())
                    .subtract(orderItem.getIntegrationAmount());
            orderItem.setRealAmount(realAmount);
        }
    }

    /**
     * 锁定商品
     * @param cartPromotionItemList
     */
    private void lockStock(List<CartPromotionItem> cartPromotionItemList) {
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(cartPromotionItem.getProductSkuId());
            skuStock.setLockStock(skuStock.getLockStock() + cartPromotionItem.getQuantity());
            skuStockMapper.updateByPrimaryKeySelective(skuStock);
        }
    }

    /**
     * 计算订单活动优惠
     * @param orderItemList
     * @return
     */
    private BigDecimal calcPromotionAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal promotionAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getPromotionAmount() != null) {
                promotionAmount = promotionAmount.add(orderItem.getPromotionAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return promotionAmount;
    }

    /**
     * 获取订单促销信息
     * @param orderItemList
     * @return
     */
    private String getOrderPromotionInfo(List<OmsOrderItem> orderItemList) {
        StringBuilder sb = new StringBuilder();
        for (OmsOrderItem orderItem : orderItemList) {
            sb.append(orderItem.getPromotionName());
            sb.append(";");
        }
        String result = sb.toString();
        if (result.endsWith(";")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * 计算订单优惠券金额
     * @param orderItemList
     * @return
     */
    private BigDecimal calcCouponAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal couponAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getCouponAmount() != null) {
                couponAmount = couponAmount.add(orderItem.getCouponAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return couponAmount;
    }

    /**
     * 计算订单积分金额
     * @param orderItemList
     * @return
     */
    private BigDecimal calcIntegrationAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal integrationAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getIntegrationAmount() != null) {
                integrationAmount = integrationAmount.add(orderItem.getIntegrationAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return integrationAmount;
    }

    /**
     * 计算订单应付金额
     * @param order
     * @return
     */
    private BigDecimal calcPayAmount(OmsOrder order) {
        //总金额+运费-促销优惠-优惠券优惠-积分抵扣
        BigDecimal payAmount = order.getTotalAmount()
                .add(order.getFreightAmount())
                .subtract(order.getPromotionAmount())
                .subtract(order.getCouponAmount())
                .subtract(order.getIntegrationAmount());
        return payAmount;
    }


    /**
     * 计算该订单赠送的积分
     */
    private Integer calcGifIntegration(List<OmsOrderItem> orderItemList) {
        int sum = 0;
        for (OmsOrderItem orderItem : orderItemList) {
            sum += orderItem.getGiftIntegration() * orderItem.getProductQuantity();
        }
        return sum;
    }

    /**
     * 计算该订单赠送的成长值
     */
    private Integer calcGiftGrowth(List<OmsOrderItem> orderItemList) {
        Integer sum = 0;
        for (OmsOrderItem orderItem : orderItemList) {
            sum = sum + orderItem.getGiftGrowth() * orderItem.getProductQuantity();
        }
        return sum;
    }

    /**
     * 生成18位订单编号:8位日期+2位平台号码+2位支付方式+6位以上自增id
     */
    // TODO 去构建全局uuid
    private String generateOrderSn(OmsOrder order) {
        StringBuilder sb = new StringBuilder();
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String key = REDIS_DATABASE+":"+ REDIS_KEY_ORDER_ID + date;
        Long increment = redisService.incr(key, 1);
        sb.append(date);
        sb.append(String.format("%02d", order.getSourceType()));
        sb.append(String.format("%02d", order.getPayType()));
        String incrementStr = increment.toString();
        if (incrementStr.length() <= 6) {
            sb.append(String.format("%06d", increment));
        } else {
            sb.append(incrementStr);
        }
        return sb.toString();
    }

    /**
     * 将优惠券信息更改为指定状态
     *
     * @param couponId  优惠券id
     * @param memberId  会员id
     * @param useStatus 0->未使用；1->已使用
     */
    private void updateCouponStatus(Long couponId, Long memberId, Integer useStatus) {
        if (couponId == null) return;
        //查询第一张优惠券
        SmsCouponHistoryExample example = new SmsCouponHistoryExample();
        example.createCriteria().andMemberIdEqualTo(memberId)
                .andCouponIdEqualTo(couponId).andUseStatusEqualTo(useStatus == 0 ? 1 : 0);
        List<SmsCouponHistory> couponHistoryList = couponHistoryMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(couponHistoryList)) {
            SmsCouponHistory couponHistory = couponHistoryList.get(0);
            couponHistory.setUseTime(new Date());
            couponHistory.setUseStatus(useStatus);
            couponHistoryMapper.updateByPrimaryKeySelective(couponHistory);
        }
    }

    /**
     * 删除下单商品的购物车信息
     * @param cartPromotionItemList
     * @param currentMember
     */
    private void deleteCartItemList(List<CartPromotionItem> cartPromotionItemList, UmsMember currentMember) {
        List<Long> ids = new ArrayList<>();
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            ids.add(cartPromotionItem.getId());
        }
        cartItemService.delete(currentMember.getId(), ids);
    }
}
