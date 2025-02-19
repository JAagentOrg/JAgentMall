package shop.jagentmall.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.jagentmall.service.AlipayService;

/**
 * @Title: ClosePayReceiver
 * @Author: [tianyou]
 * @Date: 2025/2/19
 * @Description: 订单关闭消息消费者
 */
@Component
@RabbitListener(queues = "mall.pay.close")
public class ClosePayReceiver {
    private static Logger LOGGER = LoggerFactory.getLogger(ClosePayReceiver.class);
    @Autowired
    private AlipayService alipayService;
    @RabbitHandler
    public void handle(String outTradeNo){
        alipayService.colsePay(outTradeNo);
        LOGGER.info("process outTradeNo:{}",outTradeNo);
    }
}
