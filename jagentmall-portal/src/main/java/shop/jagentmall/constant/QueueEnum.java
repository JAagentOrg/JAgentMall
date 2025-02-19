package shop.jagentmall.constant;

import lombok.Data;
import lombok.Getter;

/**
 * @Title: QueueEnum
 * @Author: [tianyou]
 * @Date: 2025/2/16
 * @Description: 消息队列枚举配置
 */
@Getter
public enum QueueEnum {
    /**
     * 消息通知队列
     */
    QUEUE_ORDER_CANCEL("mall.order.direct", "mall.order.cancel", "mall.order.cancel"),
    /**
     * 消息通知ttl队列
     */
    QUEUE_TTL_ORDER_CANCEL("mall.order.direct.ttl", "mall.order.cancel.ttl", "mall.order.cancel.ttl"),

    /**
     * 订单支付关闭消息通知队列
     */
    QUEUE_PAY_CLOSE("mall.pay.direct", "mall.pay.close", "mall.pay.close"),

    /**
     * 订单支付关闭消息ttl队列
     */
    QUEUE_TTL_PAY_CLOSE("mall.pay.direct.ttl", "mall.pay.close.ttl", "mall.pay.close.ttl");

    /**
     * 交换名称
     */
    private String exchange;
    /**
     * 队列名称
     */
    private String name;
    /**
     * 路由键
     */
    private String routeKey;

    QueueEnum(String exchange, String name, String routeKey) {
        this.exchange = exchange;
        this.name = name;
        this.routeKey = routeKey;
    }
}
