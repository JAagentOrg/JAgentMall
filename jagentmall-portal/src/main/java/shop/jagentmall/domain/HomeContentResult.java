package shop.jagentmall.domain;

import lombok.Data;
import shop.jagentmall.model.CmsSubject;
import shop.jagentmall.model.PmsBrand;
import shop.jagentmall.model.PmsProduct;
import shop.jagentmall.model.SmsHomeAdvertise;

import java.util.List;

/**
 * @Title: HomeContentResult
 * @Author: [tianyou]
 * @Date: 2025/2/20
 * @Description: 首页内容
 */
@Data
public class HomeContentResult {
    //轮播广告
    private List<SmsHomeAdvertise> advertiseList;
    //推荐品牌
    private List<PmsBrand> brandList;
    //当前秒杀场次
    private HomeFlashPromotion homeFlashPromotion;
    //新品推荐
    private List<PmsProduct> newProductList;
    //人气推荐
    private List<PmsProduct> hotProductList;
    //推荐专题
    private List<CmsSubject> subjectList;
}
