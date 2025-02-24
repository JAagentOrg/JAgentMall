package shop.jagentmall.service;

import org.springframework.transaction.annotation.Transactional;
import shop.jagentmall.domain.CartProduct;
import shop.jagentmall.domain.CartPromotionItem;
import shop.jagentmall.model.OmsCartItem;

import java.util.List;

/**
 * @Title: OmsCartItemService
 * @Author: [tianyou]
 * @Date: 2025/2/16
 * @Description: 购物车管理service
 */
public interface OmsCartItemService {

    /**
     * 添加商品进入购物车，如果重复了，则直接增加数量即可
     * @param cartItem
     * @return
     */
    @Transactional
    int add(OmsCartItem cartItem);

    /**
     * 根据会员id去获取会员的购物车列表
     * @param id
     * @return
     */
    List<OmsCartItem> list(Long id);

    /**
     * 根据会员id去获取会员的购物车列表，并且带促销信息
     * @param id
     * @param cartIds
     * @return
     */
    List<CartPromotionItem> listPromotion(Long id, List<Long> cartIds);

    /**
     * 修改购物车中商品的数量
     * @param id
     * @param memberId
     * @param quantity
     * @return
     */
    @Transactional
    int updateQuantity(Long id, Long memberId, Integer quantity);

    /**
     * 获取商品的规格，主要是用于修改规格时
     * @param productId
     * @return
     */
    CartProduct getCartProduct(Long productId);

    /**
     * 用于修改购物车中商品的规格
     * @param cartItem
     * @return
     */
    @Transactional
    int updateAttr(OmsCartItem cartItem);

    /**
     * 删除购物车中的商品
     * @param memberId
     * @param ids
     * @return
     */
    @Transactional
    int delete(Long memberId, List<Long> ids);

    /**
     * 清除会员购物车里所有商品
     * @param memberId
     * @return
     */
    @Transactional
    int clear(Long memberId);
}
