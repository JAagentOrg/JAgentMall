package shop.jagentmall.domain;

import shop.jagentmall.model.OmsOrder;
import shop.jagentmall.model.OmsOrderItem;

import java.util.List;

/**
 * 天天进步
 *
 * @Author: ztboxs
 * @Date: 2025/03/03
 * @Description:
 */
public class OmsOrderDetail extends OmsOrder {
    private List<OmsOrderItem> orderItemList;

    public List<OmsOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OmsOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
