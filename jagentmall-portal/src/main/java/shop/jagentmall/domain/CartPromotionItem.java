package shop.jagentmall.domain;

import lombok.Data;
import shop.jagentmall.model.OmsCartItem;

import java.math.BigDecimal;

/**
 * @Title: CartPromotionItem
 * @Author: [tianyou]
 * @Date: 2025/2/17
 * @Description: 购物车商品信息，带促销信息
 */
@Data
public class CartPromotionItem extends OmsCartItem {
    //促销活动信息
    private String promotionMessage;
    //促销活动减去的金额，针对每个商品
    private BigDecimal reduceAmount;
    //商品的真实库存（剩余库存-锁定库存）
    private Integer realStock;
    //购买商品赠送积分
    private Integer integration;
    //购买商品赠送成长值
    private Integer growth;
}
