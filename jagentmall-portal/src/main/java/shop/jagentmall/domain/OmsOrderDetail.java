package shop.jagentmall.domain;

import shop.jagentmall.model.OmsOrder;
import shop.jagentmall.model.OmsOrderItem;

import java.util.List;

/**
 * @Title: OmsOrderDetail
 * @Author: [tianyou]
 * @Date: 2025/2/18
 * @Description: 订单详细信息
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
