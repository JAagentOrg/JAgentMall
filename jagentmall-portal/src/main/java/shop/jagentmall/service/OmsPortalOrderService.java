package shop.jagentmall.service;

import org.springframework.transaction.annotation.Transactional;
import shop.jagentmall.api.CommonPage;
import shop.jagentmall.domain.ConfirmOrderResult;
import shop.jagentmall.domain.OmsOrderDetail;
import shop.jagentmall.domain.OrderParam;

import java.util.List;
import java.util.Map;

/**
 * @Title: OmsPortalOrderService
 * @Author: [tianyou]
 * @Date: 2025/2/18
 * @Description: 前台订单管理service
 */
public interface OmsPortalOrderService {
    /**
     * 生成订单确认单
     * @param cartIds
     * @return
     */
    ConfirmOrderResult generateConfirmOrder(List<Long> cartIds);

    /**
     * 生成实际订单
     * @param orderParam
     * @return
     */
    @Transactional
    Map<String, Object> generateOrder(OrderParam orderParam);

    /**
     * 发送延迟消息取消订单
     */
    void sendDelayMessageCancelOrder(Long orderId);

    /**
     * 延迟消息消费，取消订单
     * @param orderId
     */
    @Transactional
    void cancelOrder(Long orderId);

    /**
     * 按状态分页获取用户订单列表
     * @param status
     * @param pageNum
     * @param pageSize
     * @return
     */
    CommonPage<OmsOrderDetail> list(Integer status, Integer pageNum, Integer pageSize);

    /**
     * 根据订单id获取订单详细信息
     * @param orderId
     * @return
     */
    OmsOrderDetail detail(Long orderId);

    /**
     * 用户确认收货
     * @param orderId
     */
    void confirmReceiveOrder(Long orderId);

    /**
     * 用户删除订单
     * @param orderId
     */
    void deleteOrder(Long orderId);
}
