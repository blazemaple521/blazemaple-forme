package com.blazemaple.forum.event;

import com.blazemaple.forum.domain.entity.Event;
import com.blazemaple.forum.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @author BlazeMaple
 * @description
 * @date 2023/7/2 21:56
 */
@Component
@RequiredArgsConstructor
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    public void fireMessageEvent(Event event, String exchageName,String routingKey){
        rabbitTemplate.convertAndSend(exchageName,routingKey, JsonUtils.objectToJson(event));
    }

}
