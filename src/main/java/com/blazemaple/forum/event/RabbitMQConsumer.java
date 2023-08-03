package com.blazemaple.forum.event;


import com.alibaba.fastjson.JSONObject;
import com.blazemaple.forum.common.ForumConstant;
import com.blazemaple.forum.config.RabbitMQConfig;
import com.blazemaple.forum.domain.entity.DiscussPost;
import com.blazemaple.forum.domain.entity.Event;
import com.blazemaple.forum.domain.entity.Msg;
import com.blazemaple.forum.service.DiscussPostService;
import com.blazemaple.forum.service.ElasticSearchService;
import com.blazemaple.forum.service.MessageService;
import com.blazemaple.forum.util.ForumUtil;
import com.blazemaple.forum.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author BlazeMaple
 * @description
 * @date 2023/6/10 16:19
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConsumer implements ForumConstant {

    private final MessageService messageService;

    private final DiscussPostService discussPostService;

    private final ElasticSearchService elasticSearchService;


    @RabbitListener(queues = {RabbitMQConfig.QUEUE_SYS_MSG})
    public void watchMsgQueue(String payload, Message message) {

        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info(payload);
        log.info(routingKey);
        Event event = JsonUtils.jsonToPojo(payload, Event.class);
        if (event==null){
            log.error("消息格式错误");
            return;
        }
        //发送站内通知
        Msg msg=new Msg();
        msg.setFromId(SYSTEM_USER_ID);
        msg.setToId(event.getEntityUserId());
        // 此处存的是主题
        msg.setConversationId(event.getTopic());
        msg.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        // 处理map中内容
        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }

        msg.setContent(JSONObject.toJSONString(content));
        messageService.insertMessage(msg);

    }

    @RabbitListener(queues = {RabbitMQConfig.QUEUE_PUBLISH})
    public void watchPublishQueue(String payload){
        Event event = JsonUtils.jsonToPojo(payload, Event.class);
        if (event==null){
            log.error("消息格式错误");
            return;
        }
        if (event.getTopic().equals(TOPIC_DELETE)) {
            elasticSearchService.deleteDiscussPost(event.getEntityId());
        }else {
            DiscussPost discussPost = discussPostService.getById(event.getEntityId());
            elasticSearchService.savaDiscussPost(discussPost);
        }
    }




}
