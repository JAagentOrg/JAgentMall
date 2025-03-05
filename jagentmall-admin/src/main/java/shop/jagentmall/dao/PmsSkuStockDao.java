package shop.jagentmall.dao;

import org.apache.ibatis.annotations.Param;
import shop.jagentmall.model.PmsSkuStock;

import java.util.List;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/03/02
 * @Description: 自定义商品SKU管理Dao
 */
public interface PmsSkuStockDao {

    /**
     * 批量插入操作
     */
    int insertList(@Param("list") List<PmsSkuStock> skuStockList);
}
