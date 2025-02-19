package shop.jagentmall.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.jagentmall.constant.QueueEnum;

/**
 * @Title: RabbitMqConfig
 * @Author: [tianyou]
 * @Date: 2025/2/16
 * @Description: 消息队列配置文件
 */
@Configuration
public class RabbitMqConfig {
    /**
     * 订单消息实际消费队列所绑定的交换机
     */
    @Bean
    DirectExchange orderDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_ORDER_CANCEL.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 支付关闭实际消费队列所绑定的交换机
     */
    @Bean
    DirectExchange payDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_PAY_CLOSE.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 订单延迟队列队列所绑定的交换机
     */
    @Bean
    DirectExchange orderTtlDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 支付延迟队列队列所绑定的交换机
     */
    @Bean
    DirectExchange payTtlDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(QueueEnum.QUEUE_TTL_PAY_CLOSE.getExchange())
                .durable(true)
                .build();
    }

    /**
     * 订单实际消费队列
     */
    @Bean
    public Queue orderQueue() {
        return new Queue(QueueEnum.QUEUE_ORDER_CANCEL.getName());
    }

    /**
     * 支付关闭实际消费队列
     */
    @Bean
    public Queue payQueue() {
        return new Queue(QueueEnum.QUEUE_PAY_CLOSE.getName());
    }

    /**
     * 订单延迟队列（死信队列）
     */
    @Bean
    public Queue orderTtlQueue() {
        return QueueBuilder
                .durable(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getName())
                .withArgument("x-dead-letter-exchange", QueueEnum.QUEUE_ORDER_CANCEL.getExchange())//到期后转发的交换机
                .withArgument("x-dead-letter-routing-key", QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey())//到期后转发的路由键
                .build();
    }

    /**
     * 支付关闭延迟队列（死信队列）
     */
    @Bean
    public Queue payTtlQueue() {
        return QueueBuilder
                .durable(QueueEnum.QUEUE_TTL_PAY_CLOSE.getName())
                .withArgument("x-dead-letter-exchange", QueueEnum.QUEUE_PAY_CLOSE.getExchange())//到期后转发的交换机
                .withArgument("x-dead-letter-routing-key", QueueEnum.QUEUE_PAY_CLOSE.getRouteKey())//到期后转发的路由键
                .build();
    }

    /**
     * 将订单队列绑定到交换机
     */
    @Bean
    Binding orderBinding(DirectExchange orderDirect, Queue orderQueue){
        return BindingBuilder
                .bind(orderQueue)
                .to(orderDirect)
                .with(QueueEnum.QUEUE_ORDER_CANCEL.getRouteKey());
    }

    /**
     * 将支付关闭队列绑定到交换机
     */
    @Bean
    Binding payBinding(DirectExchange payDirect, Queue payQueue){
        return BindingBuilder
                .bind(payQueue)
                .to(payDirect)
                .with(QueueEnum.QUEUE_PAY_CLOSE.getRouteKey());
    }

    /**
     * 将订单延迟队列绑定到交换机
     */
    @Bean
    Binding orderTtlBinding(DirectExchange orderTtlDirect,Queue orderTtlQueue){
        return BindingBuilder
                .bind(orderTtlQueue)
                .to(orderTtlDirect)
                .with(QueueEnum.QUEUE_TTL_ORDER_CANCEL.getRouteKey());
    }

    /**
     * 将支付关闭延迟队列绑定到交换机
     */
    @Bean
    Binding payTtlBinding(DirectExchange payTtlDirect,Queue payTtlQueue){
        return BindingBuilder
                .bind(payTtlQueue)
                .to(payTtlDirect)
                .with(QueueEnum.QUEUE_TTL_PAY_CLOSE.getRouteKey());
    }
}
