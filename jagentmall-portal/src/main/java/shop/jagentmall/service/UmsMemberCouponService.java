package shop.jagentmall.service;

import shop.jagentmall.domain.CartPromotionItem;
import shop.jagentmall.domain.SmsCouponHistoryDetail;

import java.util.List;

/**
 * @Title: UmsMemberCouponService
 * @Author: [tianyou]
 * @Date: 2025/2/18
 * @Description: 用户优惠券管理service
 */
public interface UmsMemberCouponService {
    /**
     * 获取会员购物车中可用的优惠券
     * @param cartPromotionItemList
     * @param type   1-->返回可用优惠券  2-->返回不可用优惠券
     * @return
     */
    List<SmsCouponHistoryDetail> listCart(List<CartPromotionItem> cartPromotionItemList, Integer type);
}
