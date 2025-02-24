package shop.jagentmall.service;

import shop.jagentmall.domain.CartPromotionItem;
import shop.jagentmall.model.OmsCartItem;

import java.util.List;

/**
 * @Title: OmsPromotionService
 * @Author: [tianyou]
 * @Date: 2025/2/17
 * @Description: 促销信息service
 */
public interface OmsPromotionService {
    List<CartPromotionItem> calcCartPromotion(List<OmsCartItem> cartItemList);
}
