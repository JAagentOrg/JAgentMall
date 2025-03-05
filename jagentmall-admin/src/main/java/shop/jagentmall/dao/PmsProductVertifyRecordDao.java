package shop.jagentmall.dao;

import org.apache.ibatis.annotations.Param;
import shop.jagentmall.model.PmsProductVertifyRecord;

import java.util.List;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/03/02
 * @Description: 自定义商品审核日志管理Dao
 */
public interface PmsProductVertifyRecordDao {

    /**
     * 批量创建
     */
    int insertList(@Param("list") List<PmsProductVertifyRecord> list);

}
