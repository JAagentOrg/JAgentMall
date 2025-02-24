package shop.jagentmall.service;

import shop.jagentmall.domain.PmsPortalProductDetail;
import shop.jagentmall.model.PmsProduct;

import java.util.List;

/**
 * @Title: PmsPortalProductService
 * @Author: [tianyou]
 * @Date: 2025/2/15
 * @Description: 前台商品管理service
 */
public interface PmsPortalProductService {
    /**
     * 综合搜索商品
     */
    List<PmsProduct> search(String keyword, Long brandId, Long productCategoryId, Integer pageNum, Integer pageSize, Integer sort);



    /**
     * 获取前台商品详情
     */
    PmsPortalProductDetail detail(Long id);
}
