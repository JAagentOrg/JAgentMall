package shop.jagentmall.service;

import shop.jagentmall.api.CommonPage;
import shop.jagentmall.model.PmsBrand;
import shop.jagentmall.model.PmsProduct;

import java.util.List;

/**
 * @Title: PortalBrandService
 * @Author: [tianyou]
 * @Date: 2025/2/24
 * @Description: 前台品牌管理Service
 */
public interface PortalBrandService {
    /**
     * 分页获取推荐品牌
     */
    List<PmsBrand> recommendList(Integer pageNum, Integer pageSize);

    /**
     * 获取品牌详情
     */
    PmsBrand detail(Long brandId);

    /**
     * 分页获取品牌关联商品
     */
    CommonPage<PmsProduct> productList(Long brandId, Integer pageNum, Integer pageSize);
}
