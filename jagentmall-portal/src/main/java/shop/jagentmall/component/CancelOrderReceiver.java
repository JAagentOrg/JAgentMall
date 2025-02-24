package shop.jagentmall.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.jagentmall.idempotent.annotation.Idempotent;
import shop.jagentmall.idempotent.enums.IdempotentSceneEnum;
import shop.jagentmall.idempotent.enums.IdempotentTypeEnum;
import shop.jagentmall.service.OmsPortalOrderService;

/**
 * @Title: CancelOrderReceiver
 * @Author: [tianyou]
 * @Date: 2025/2/18
 * @Description: 订单消息消费者
 */
@Component
@RabbitListener(queues = "mall.order.cancel")
public class CancelOrderReceiver {
    private static Logger LOGGER = LoggerFactory.getLogger(CancelOrderReceiver.class);

    @Autowired
    private OmsPortalOrderService portalOrderService;
    @Idempotent(
            uniqueKeyPrefix = "JAgentMall-PortalOrder:delay_cancel_order:",
            key = "T(String).valueOf(#orderId)",
            type = IdempotentTypeEnum.SPEL,
            scene = IdempotentSceneEnum.MQ,
            keyTimeout = 7200L
    )
    @RabbitHandler
    public void handle(Long orderId){
        portalOrderService.cancelOrder(orderId);
        LOGGER.info("process orderId:{}",orderId);
    }
}
