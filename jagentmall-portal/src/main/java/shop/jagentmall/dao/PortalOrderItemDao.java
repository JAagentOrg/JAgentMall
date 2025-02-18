package shop.jagentmall.dao;

import org.apache.ibatis.annotations.Param;
import shop.jagentmall.model.OmsOrderItem;

import java.util.List;

/**
 * @Title: PortalOrderItemDao
 * @Author: [tianyou]
 * @Date: 2025/2/18
 * @Description: 订单子订单Dao类
 */
public interface PortalOrderItemDao {
    int insertList(@Param("list") List<OmsOrderItem> list);
}
