package com.blazemaple.forum.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author BlazeMaple
 * @description
 * @date 2023/5/27 16:54
 * <p>
 * 定义交换机 定义队列 创建交换机 创建队列 队列和交换机进行绑定
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_MSG = "exchange_msg";
    public static final String  QUEUE_SYS_MSG = "queue_sys_msg";

    public static final String EXCHANGE_PUBLISH = "exchange_publish";
    public static final String  QUEUE_PUBLISH = "queue_publish";

    public static final String EXCHANGE_SHARE = "exchange_share";
    public static final String  QUEUE_SHARE = "queue_share";


    @Bean(EXCHANGE_MSG)
    public Exchange exchangeMsg() {
        return ExchangeBuilder
            .topicExchange(EXCHANGE_MSG)      //类型
            .durable(true)         //持久化
            .build();
    }

    @Bean(QUEUE_SYS_MSG)
    public Queue queueMsg() {
        return new Queue(QUEUE_SYS_MSG);
    }

    @Bean
    public Binding bindingMsg(@Qualifier(EXCHANGE_MSG) Exchange exchange,
                           @Qualifier(QUEUE_SYS_MSG) Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with("sys.msg.*").noargs();

    }

    @Bean(EXCHANGE_PUBLISH)
    public Exchange exchangePublish() {
        return ExchangeBuilder
            .topicExchange(EXCHANGE_PUBLISH)      //类型
            .durable(true)         //持久化
            .build();
    }

    @Bean(QUEUE_PUBLISH)
    public Queue queuePublish() {
        return new Queue(QUEUE_PUBLISH);
    }

    @Bean
    public Binding bindingPublish(@Qualifier(EXCHANGE_PUBLISH) Exchange exchange,
        @Qualifier(QUEUE_PUBLISH) Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with("publish.*").noargs();
    }

    @Bean(EXCHANGE_SHARE)
    public Exchange exchangeShare() {
        return ExchangeBuilder
            .topicExchange(EXCHANGE_SHARE)      //类型
            .durable(true)         //持久化
            .build();
    }

    @Bean(QUEUE_SHARE)
    public Queue queueShare() {
        return new Queue(QUEUE_SHARE);
    }

    @Bean
    public Binding bindingShare(@Qualifier(EXCHANGE_SHARE) Exchange exchange,
        @Qualifier(QUEUE_SHARE) Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with("share.*").noargs();

    }

}
