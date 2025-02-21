package shop.jagentmall.dao;

import org.apache.ibatis.annotations.Param;
import shop.jagentmall.domain.FlashPromotionProduct;
import shop.jagentmall.model.CmsSubject;
import shop.jagentmall.model.PmsBrand;
import shop.jagentmall.model.PmsProduct;

import java.util.List;

/**
 * @Title: HomeDao
 * @Author: [tianyou]
 * @Date: 2025/2/20
 * @Description: 首页管理Dao
 */
public interface HomeDao {
    /**
     * 获取推荐品牌
     */
    List<PmsBrand> getRecommendBrandList(@Param("offset") Integer offset, @Param("limit") Integer limit);

    /**
     * 获取秒杀商品
     */
    List<FlashPromotionProduct> getFlashProductList(@Param("flashPromotionId") Long flashPromotionId, @Param("sessionId") Long sessionId);

    /**
     * 获取新品推荐
     */
    List<PmsProduct> getNewProductList(@Param("offset") Integer offset, @Param("limit") Integer limit);
    /**
     * 获取人气推荐
     */
    List<PmsProduct> getHotProductList(@Param("offset") Integer offset,@Param("limit") Integer limit);

    /**
     * 获取推荐专题
     */
    List<CmsSubject> getRecommendSubjectList(@Param("offset") Integer offset, @Param("limit") Integer limit);
}
