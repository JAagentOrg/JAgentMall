package shop.jagentmall.service;

import org.springframework.transaction.annotation.Transactional;
import shop.jagentmall.domain.ConfirmOrderResult;
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
}
