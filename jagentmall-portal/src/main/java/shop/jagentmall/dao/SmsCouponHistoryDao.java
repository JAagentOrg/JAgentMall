package shop.jagentmall.dao;

import org.apache.ibatis.annotations.Param;
import shop.jagentmall.domain.SmsCouponHistoryDetail;
import shop.jagentmall.model.SmsCoupon;

import java.util.List;

/**
 * @Title: SmsCouponHistoryDao
 * @Author: [tianyou]
 * @Date: 2025/2/18
 * @Description: 会员优惠卷历史Dao
 */
public interface SmsCouponHistoryDao {
    List<SmsCouponHistoryDetail> getDetailList(@Param("memberId") Long memberId);
    List<SmsCoupon> getCouponList(@Param("memberId") Long memberId, @Param("useStatus")Integer useStatus);
}
