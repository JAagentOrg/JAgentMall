package shop.jagentmall.dao;

import org.apache.ibatis.annotations.Param;
import shop.jagentmall.model.PmsProductLadder;

import java.util.List;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/03/02
 * @Description: 自定义会员阶梯价格Dao
 */
public interface PmsProductLadderDao {

    /**
     * 批量创建
     */
    int insertList(@Param("list") List<PmsProductLadder> productLadderList);

}
