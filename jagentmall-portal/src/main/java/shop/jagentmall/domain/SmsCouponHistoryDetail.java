package shop.jagentmall.domain;

import lombok.Data;
import shop.jagentmall.model.SmsCoupon;
import shop.jagentmall.model.SmsCouponHistory;
import shop.jagentmall.model.SmsCouponProductCategoryRelation;
import shop.jagentmall.model.SmsCouponProductRelation;

import java.util.List;

/**
 * @Title: SmsCouponHistoryDetail
 * @Author: [tianyou]
 * @Date: 2025/2/18
 * @Description: 优惠券领取历史详细封装
 */
@Data
public class SmsCouponHistoryDetail extends SmsCouponHistory {
    private SmsCoupon coupon;
    //优惠券关联商品
    private List<SmsCouponProductRelation> productRelationList;
    //优惠券关联商品分类
    private List<SmsCouponProductCategoryRelation> categoryRelationList;
}
