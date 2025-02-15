package shop.jagentmall.dao;

import org.apache.ibatis.annotations.Param;
import shop.jagentmall.domain.CartProduct;
import shop.jagentmall.domain.PromotionProduct;
import shop.jagentmall.model.SmsCoupon;

import java.util.List;

/**
 * @Title: PortalProductDao
 * @Author: [tianyou]
 * @Date: 2025/2/15 23:12
 * @Description: 前台系统自定义商品Dao
 */
public interface PortalProductDao {
    CartProduct getCartProduct(@Param("id") Long id);
    List<PromotionProduct> getPromotionProductList(@Param("ids") List<Long> ids);
    List<SmsCoupon> getAvailableCouponList(@Param("productId") Long productId, @Param("productCategoryId")Long productCategoryId);
}
