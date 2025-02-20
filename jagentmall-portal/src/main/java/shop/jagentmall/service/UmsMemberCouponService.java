package shop.jagentmall.service;

import org.springframework.transaction.annotation.Transactional;
import shop.jagentmall.domain.CartPromotionItem;
import shop.jagentmall.domain.SmsCouponHistoryDetail;
import shop.jagentmall.model.SmsCoupon;
import shop.jagentmall.model.SmsCouponHistory;

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

    /**
     * 会员领取优惠券
     * @param couponId
     */
    @Transactional
    void add(Long couponId);

    /**
     * 获取会员优惠券历史列表
     * @param useStatus
     * @return
     */
    List<SmsCouponHistory> listHistory(Integer useStatus);

    /**
     * 获取会员优惠券
     * @param useStatus
     * @return
     */
    List<SmsCoupon> list(Integer useStatus);

    /**
     * 获取产品相关优惠券
     * @param productId
     * @return
     */
    List<SmsCoupon> listByProduct(Long productId);
}
