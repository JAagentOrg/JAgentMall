package shop.jagentmall.dao;

import org.apache.ibatis.annotations.Param;
import shop.jagentmall.domain.EsProduct;

import java.util.List;

/**
 * @Title: EsProductDao
 * @Author: [tianyou]
 * @Date: 2025/2/21
 * @Description: 自定义搜索Dao
 */
public interface EsProductDao {
    /**
     * 根据id搜索商品
     * @param id
     * @return
     */
    List<EsProduct> getAllEsProductList(@Param("id") Long id);
}
