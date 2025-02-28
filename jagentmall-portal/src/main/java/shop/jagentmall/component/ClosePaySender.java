package shop.jagentmall.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.jagentmall.constant.QueueEnum;

/**
 * @Title: ClosePaySender
 * @Author: [tianyou]
 * @Date: 2025/2/19
 * @Description: 支付定时关闭发送者
 */
@Component
public class ClosePaySender {
    private static Logger LOGGER = LoggerFactory.getLogger(CancelOrderSender.class);
    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendMessage(String outTradeNo,final long delayTimes){
        //给延迟队列发送消息
        amqpTemplate.convertAndSend(QueueEnum.QUEUE_TTL_PAY_CLOSE.getExchange(),
                QueueEnum.QUEUE_TTL_PAY_CLOSE.getRouteKey(),
                outTradeNo,
                new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        //给消息设置延迟毫秒值
                        message.getMessageProperties().setExpiration(String.valueOf(delayTimes));
                        return message;
                    }
                }
        );
        LOGGER.info("send orderId:{}",outTradeNo);
    }
}
