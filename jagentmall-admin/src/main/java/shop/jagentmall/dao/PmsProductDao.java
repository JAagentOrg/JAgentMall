package shop.jagentmall.dao;

import org.apache.ibatis.annotations.Param;
import shop.jagentmall.dto.PmsProductResult;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/03/02
 * @Description: 自定义商品管理Dao
 */
public interface PmsProductDao {

    /**
     * 获取商品编辑信息
     */
    PmsProductResult getUpdateInfo(@Param("id") Long id);

}
