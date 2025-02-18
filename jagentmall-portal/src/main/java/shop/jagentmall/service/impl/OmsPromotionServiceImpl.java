package shop.jagentmall.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.jagentmall.dao.PortalProductDao;
import shop.jagentmall.domain.CartPromotionItem;
import shop.jagentmall.domain.PromotionProduct;
import shop.jagentmall.model.OmsCartItem;
import shop.jagentmall.model.PmsProductFullReduction;
import shop.jagentmall.model.PmsProductLadder;
import shop.jagentmall.model.PmsSkuStock;
import shop.jagentmall.service.OmsPromotionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @Title: OmsPromotionServiceImpl
 * @Author: [tianyou]
 * @Date: 2025/2/17
 * @Description: 商品促销信息service实现类
 */
@Service
public class OmsPromotionServiceImpl implements OmsPromotionService {
    @Autowired
    private PortalProductDao portalProductDao;

    @Override
    public List<CartPromotionItem> calcCartPromotion(List<OmsCartItem> cartItemList) {
        // 先根据商品的productId归类，即根据spu进行归类
        Map<Long,List<OmsCartItem>> productCarMap = groupCartItemBySpu(cartItemList);
        // 查询商品的优惠信息
        List<PromotionProduct> promotionProductList = getPromotionProductList(cartItemList);
        // 计算商品的促销信息
        List<CartPromotionItem> cartPromotionItemList = new ArrayList<>();
        for(Map.Entry<Long,List<OmsCartItem>> entry: productCarMap.entrySet()){
            // 获取当前产品的促销信息
            Long productId = entry.getKey();
            PromotionProduct promotionProduct = getPromotionProductById(productId,promotionProductList);
            // 获取购物车中的当前商品列表
            List<OmsCartItem> itemList = entry.getValue();
            // 获取当前商品的促销类型
            Integer promotionType = promotionProduct.getPromotionType();

            if(promotionType.equals(1)){
                // 以单个商品促销
                for(OmsCartItem cartItem: itemList){
                    CartPromotionItem cartPromotionItem = new CartPromotionItem();
                    BeanUtils.copyProperties(cartItem,cartPromotionItem);
                    // 获取商品原价
                    PmsSkuStock skuStock = getSkuStockById(promotionProduct,cartItem.getProductSkuId());
                    BigDecimal originalPrice = skuStock.getPrice();
                    // 设置促销减免的金额
                    cartPromotionItem.setReduceAmount(originalPrice.subtract(skuStock.getPromotionPrice()));
                    // 设置原价
                    cartPromotionItem.setPrice(originalPrice);
                    // 设置剩余库存
                    cartPromotionItem.setRealStock(skuStock.getStock()-skuStock.getLockStock());
                    // 设置积分和会员成长值
                    cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                    cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());

                    cartPromotionItemList.add(cartPromotionItem);
                }
            }
            else if(promotionType.equals(2)){
                // 以会员等级价格促销
            }
            else if(promotionType.equals(3)){
                // 使用阶梯价格
                int count = 0;
                for (OmsCartItem item: itemList) {
                    count += item.getQuantity();
                }
                PmsProductLadder ladder = getProductLadder(count, promotionProduct.getProductLadderList());
                if(ladder != null){
                    // 数量满足打折要求
                    for(OmsCartItem cartItem: itemList){
                        CartPromotionItem cartPromotionItem = new CartPromotionItem();
                        BeanUtils.copyProperties(cartItem,cartPromotionItem);
                        String message = getLadderPromotionMessage(ladder);
                        cartPromotionItem.setPromotionMessage(message);
                        // 获取商品原价
                        PmsSkuStock skuStock = getSkuStockById(promotionProduct,cartItem.getProductSkuId());
                        BigDecimal originalPrice = skuStock.getPrice();
                        // 设置促销减免的金额
                        cartPromotionItem.setReduceAmount(originalPrice.subtract(originalPrice.multiply(ladder.getDiscount())));
                        // 设置原价
                        cartPromotionItem.setPrice(originalPrice);
                        // 设置剩余库存
                        cartPromotionItem.setRealStock(skuStock.getStock()-skuStock.getLockStock());
                        // 设置积分和会员成长值
                        cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                        cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());

                        cartPromotionItemList.add(cartPromotionItem);
                    }
                }
                else{
                    // 数量不满足打折要求
                    handleNoReduce(cartPromotionItemList,itemList,promotionProduct);
                }
            }
            else if(promotionType.equals(4)){
                // 满减优惠
                BigDecimal totalAmount= getCartItemAmount(itemList,promotionProductList);
                PmsProductFullReduction fullReduction = getProductFullReduction(totalAmount,promotionProduct.getProductFullReductionList());
                if(fullReduction!=null){
                    for (OmsCartItem item : itemList) {
                        CartPromotionItem cartPromotionItem = new CartPromotionItem();
                        BeanUtils.copyProperties(item,cartPromotionItem);
                        String message = getFullReductionPromotionMessage(fullReduction);
                        cartPromotionItem.setPromotionMessage(message);
                        PmsSkuStock skuStock= getSkuStockById(promotionProduct, item.getProductSkuId());
                        BigDecimal originalPrice = skuStock.getPrice();
                        BigDecimal reduceAmount = originalPrice.divide(totalAmount, RoundingMode.HALF_EVEN).multiply(fullReduction.getReducePrice());
                        cartPromotionItem.setReduceAmount(reduceAmount);
                        cartPromotionItem.setRealStock(skuStock.getStock()-skuStock.getLockStock());
                        cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                        cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
                        cartPromotionItemList.add(cartPromotionItem);
                    }
                }else{
                    handleNoReduce(cartPromotionItemList,itemList,promotionProduct);
                }
            }
            else{
                handleNoReduce(cartPromotionItemList,itemList,promotionProduct);
            }
        }
        return cartPromotionItemList;
    }



    /**
     * 根据商品的productId进行归类，即根据spu归类
     * @param cartItemList
     * @return
     */
    private Map<Long,List<OmsCartItem>> groupCartItemBySpu(List<OmsCartItem> cartItemList){
        Map<Long,List<OmsCartItem>> productCarMap = new HashMap<>();
        for(OmsCartItem cartItem: cartItemList){
            List<OmsCartItem> omsCartItems = productCarMap.get(cartItem.getProductId());
            if(omsCartItems == null){
                omsCartItems = new ArrayList<>();
                omsCartItems.add(cartItem);
                productCarMap.put(cartItem.getProductId(),omsCartItems);
            }
            else{
                omsCartItems.add(cartItem);
            }
        }
        return productCarMap;
    }

    /**
     * 获取商品的所有优惠信息
     * @param cartItemList
     * @return
     */
    private List<PromotionProduct> getPromotionProductList(List<OmsCartItem> cartItemList) {
        List<Long> productIds = new ArrayList<>();
        for(OmsCartItem omsCartItem: cartItemList){
            productIds.add(omsCartItem.getProductId());
        }
        return portalProductDao.getPromotionProductList(productIds);
    }

    /**
     * 根据商品的id去查询单一商品的促销信息
     * @param productId
     * @param promotionProductList
     * @return
     */
    private PromotionProduct getPromotionProductById(Long productId, List<PromotionProduct> promotionProductList) {
        for (PromotionProduct promotionProduct: promotionProductList){
            if(promotionProduct.getId().equals(productId)){
                return promotionProduct;
            }
        }
        return null;
    }

    /**
     * 在产品促销信息中获取对映sku的信息
     * @param promotionProduct
     * @param productSkuId
     * @return
     */
    private PmsSkuStock getSkuStockById(PromotionProduct promotionProduct, Long productSkuId) {
        List<PmsSkuStock> skuStockList = promotionProduct.getSkuStockList();
        for(PmsSkuStock skuStock: skuStockList){
            if(skuStock.getId().equals(productSkuId)){
                return skuStock;
            }
        }
        return null;
    }

    /**
     * 获取对应的阶梯价格
     * @param count
     * @param productLadderList
     * @return
     */
    private PmsProductLadder getProductLadder(int count, List<PmsProductLadder> productLadderList) {
        productLadderList.sort(new Comparator<PmsProductLadder>() {
            @Override
            public int compare(PmsProductLadder o1, PmsProductLadder o2) {
                return o2.getCount() - o1.getCount();
            }
        });
        for(PmsProductLadder productLadder: productLadderList){
            if(count >= productLadder.getCount()){
                return productLadder;
            }
        }
        return null;
    }

    /**
     * 生成阶梯打折信息
     * @param ladder
     * @return
     */
    private String getLadderPromotionMessage(PmsProductLadder ladder) {
        StringBuilder sb = new StringBuilder();
        sb.append("打折优惠：");
        sb.append("满");
        sb.append(ladder.getCount());
        sb.append("件，");
        sb.append("打");
        sb.append(ladder.getDiscount().multiply(new BigDecimal(10)));
        sb.append("折");
        return sb.toString();
    }

    /**
     * 没有优惠时执行对应的方法，将信息添加到cartPromotionItemList
     * @param cartPromotionItemList
     * @param itemList
     * @param promotionProduct
     */
    private void handleNoReduce(List<CartPromotionItem> cartPromotionItemList, List<OmsCartItem> itemList, PromotionProduct promotionProduct) {
        for (OmsCartItem item : itemList) {
            CartPromotionItem cartPromotionItem = new CartPromotionItem();
            BeanUtils.copyProperties(item,cartPromotionItem);
            cartPromotionItem.setPromotionMessage("无优惠");
            cartPromotionItem.setReduceAmount(new BigDecimal(0));
            PmsSkuStock skuStock = getSkuStockById(promotionProduct,item.getProductSkuId());
            if(skuStock != null){
                cartPromotionItem.setRealStock(skuStock.getStock() - skuStock.getLockStock());
            }
            cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
            cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
            cartPromotionItemList.add(cartPromotionItem);
        }
    }

    /**
     * 计算单一产品消费了多少钱
     * @param itemList
     * @param promotionProductList
     * @return
     */
    private BigDecimal getCartItemAmount(List<OmsCartItem> itemList, List<PromotionProduct> promotionProductList) {
        BigDecimal amount = new BigDecimal(0);
        for (OmsCartItem item : itemList) {
            //计算出商品原价
            PromotionProduct promotionProduct = getPromotionProductById(item.getProductId(), promotionProductList);
            PmsSkuStock skuStock = getSkuStockById(promotionProduct,item.getProductSkuId());
            amount = amount.add(skuStock.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        return amount;
    }

    /**
     * 获取对应的满减优惠
     * @param totalAmount
     * @param productFullReductionList
     * @return
     */
    private PmsProductFullReduction getProductFullReduction(BigDecimal totalAmount, List<PmsProductFullReduction> productFullReductionList) {
        productFullReductionList.sort(new Comparator<PmsProductFullReduction>() {
            @Override
            public int compare(PmsProductFullReduction o1, PmsProductFullReduction o2) {
                return o2.getFullPrice().subtract(o1.getFullPrice()).intValue();
            }
        });
        for(PmsProductFullReduction fullReduction: productFullReductionList){
            if(totalAmount.subtract(fullReduction.getFullPrice()).intValue()>=0){
                return fullReduction;
            }
        }
        return null;
    }

    /**
     * 获取满减优惠信息
     * @param fullReduction
     * @return
     */
    private String getFullReductionPromotionMessage(PmsProductFullReduction fullReduction) {
        StringBuilder sb = new StringBuilder();
        sb.append("满减优惠：");
        sb.append("满");
        sb.append(fullReduction.getFullPrice());
        sb.append("元，");
        sb.append("减");
        sb.append(fullReduction.getReducePrice());
        sb.append("元");
        return sb.toString();
    }

}
