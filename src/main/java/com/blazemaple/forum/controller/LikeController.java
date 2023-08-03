package com.blazemaple.forum.controller;

import com.blazemaple.forum.annotation.LoginRequired;
import com.blazemaple.forum.common.ForumConstant;
import com.blazemaple.forum.config.RabbitMQConfig;
import com.blazemaple.forum.domain.entity.Event;
import com.blazemaple.forum.domain.entity.User;
import com.blazemaple.forum.event.RabbitMQConsumer;
import com.blazemaple.forum.event.RabbitMQProducer;
import com.blazemaple.forum.service.LikeService;
import com.blazemaple.forum.util.ForumUtil;
import com.blazemaple.forum.util.HostHolder;
import com.blazemaple.forum.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author BlazeMaple
 * @description
 * @date 2023/6/29 15:44
 */
@Controller
@RequiredArgsConstructor
public class LikeController implements ForumConstant {

    private final LikeService likeService;

    private final HostHolder  hostHolder;

    private final RabbitMQProducer rabbitMQProducer;

    private final RedisTemplate redisTemplate;

    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String like(Integer entityType,Integer entityId,Integer entityUserId, int postId){
        User user = hostHolder.getUser();
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        Long likeCount = likeService.findEntityLikeCount(entityType,entityId);
        Integer likeStatus = likeService.findEntityLikeStatus(user.getId(),entityType,entityId);
        Map<String,Object> map =new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);
        if (likeStatus==1){
            Event event=new Event()
                .setTopic(TOPIC_LIKE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityUserId)
                .setData("postId", postId);
            rabbitMQProducer.fireMessageEvent(event, RabbitMQConfig.EXCHANGE_MSG,"sys.msg."+TOPIC_LIKE);

        }
        //计算帖子分数
        if (entityType==ENTITY_TYPE_POST){
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey,postId);
        }
        return ForumUtil.getJSONString(0,null,map);
    }

}
