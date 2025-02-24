package shop.jagentmall.service.impl;

import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import shop.jagentmall.dao.PortalProductDao;
import shop.jagentmall.domain.CartProduct;
import shop.jagentmall.domain.CartPromotionItem;
import shop.jagentmall.mapper.OmsCartItemMapper;
import shop.jagentmall.model.OmsCartItem;
import shop.jagentmall.model.OmsCartItemExample;
import shop.jagentmall.model.UmsMember;
import shop.jagentmall.service.OmsCartItemService;
import shop.jagentmall.service.OmsPromotionService;
import shop.jagentmall.service.UmsMemberService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title: OmsCartItemServiceImpl
 * @Author: [tianyou]
 * @Date: 2025/2/16
 * @Description: 购物车管理service实现类
 */

@Service
public class OmsCartItemServiceImpl implements OmsCartItemService {

    @Autowired
    private PortalProductDao productDao;
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private OmsPromotionService promotionService;
    @Autowired
    private OmsCartItemMapper cartItemMapper;

    @Override
    public int add(OmsCartItem cartItem) {
        int count;
        UmsMember member = memberService.getCurrentMember();
        cartItem.setMemberId(member.getId());
        cartItem.setMemberNickname(member.getNickname());
        cartItem.setDeleteStatus(0);
        OmsCartItem isExistCartItem = getCartItem(cartItem);
        if(isExistCartItem == null){
            cartItem.setCreateDate(new Date());
            count = cartItemMapper.insert(cartItem);
        }
        else{
            cartItem.setModifyDate(new Date());
            isExistCartItem.setQuantity(isExistCartItem.getQuantity() + cartItem.getQuantity());
            count = cartItemMapper.updateByPrimaryKey(isExistCartItem);
        }
        return count;
    }

    @Override
    public List<OmsCartItem> list(Long id) {
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria().andDeleteStatusEqualTo(0).andMemberIdEqualTo(id);
        return cartItemMapper.selectByExample(example);
    }

    @Override
    public List<CartPromotionItem> listPromotion(Long id, List<Long> cartIds) {
        List<OmsCartItem> cartItemList = list(id);
        if(CollUtil.isNotEmpty(cartIds)){
            cartItemList = cartItemList.stream().filter(item->cartIds.contains(item.getId())).collect(Collectors.toList());
        }
        List<CartPromotionItem> cartPromotionItemList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(cartItemList)){
            cartPromotionItemList = promotionService.calcCartPromotion(cartItemList);
        }
        return cartPromotionItemList;
    }

    @Override
    public int updateQuantity(Long id, Long memberId, Integer quantity) {
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria().andMemberIdEqualTo(memberId).andIdEqualTo(id);
        OmsCartItem cartItem = new OmsCartItem();
        cartItem.setQuantity(quantity);
        return cartItemMapper.updateByExampleSelective(cartItem,example);
    }

    @Override
    public CartProduct getCartProduct(Long productId) {
        return productDao.getCartProduct(productId);
    }

    @Override
    public int updateAttr(OmsCartItem cartItem) {
        //删除原购物车信息
        OmsCartItem updateCart = new OmsCartItem();
        updateCart.setId(cartItem.getId());
        updateCart.setModifyDate(new Date());
        updateCart.setDeleteStatus(1);
        cartItemMapper.updateByPrimaryKeySelective(updateCart);
        cartItem.setId(null);
        add(cartItem);
        return 1;
    }

    @Override
    public int delete(Long memberId, List<Long> ids) {
        OmsCartItem record = new OmsCartItem();
        record.setDeleteStatus(1);
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria().andIdIn(ids).andMemberIdEqualTo(memberId);
        return cartItemMapper.updateByExampleSelective(record, example);
    }

    @Override
    public int clear(Long memberId) {
        OmsCartItem record = new OmsCartItem();
        record.setDeleteStatus(1);
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        return cartItemMapper.updateByExampleSelective(record, example);
    }

    /**
     * 获取会员用户购物车是否已存在相同的商品
     * @param cartItem
     * @return
     */
    private OmsCartItem getCartItem(OmsCartItem cartItem){
        OmsCartItemExample example = new OmsCartItemExample();
        OmsCartItemExample.Criteria criteria = example.createCriteria().andMemberIdEqualTo(cartItem.getMemberId())
                .andProductIdEqualTo(cartItem.getProductId()).andDeleteStatusEqualTo(0);
        if (cartItem.getProductSkuId() != null) {
            criteria.andProductSkuIdEqualTo(cartItem.getProductSkuId());
        }
        List<OmsCartItem> cartItemList = cartItemMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(cartItemList)) {
            return cartItemList.get(0);
        }
        return null;
    }
}
