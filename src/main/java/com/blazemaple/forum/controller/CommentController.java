package com.blazemaple.forum.controller;

import com.blazemaple.forum.common.ForumConstant;
import com.blazemaple.forum.config.RabbitMQConfig;
import com.blazemaple.forum.domain.entity.Comment;
import com.blazemaple.forum.domain.entity.DiscussPost;
import com.blazemaple.forum.domain.entity.Event;
import com.blazemaple.forum.event.RabbitMQProducer;
import com.blazemaple.forum.service.CommentService;
import com.blazemaple.forum.service.DiscussPostService;
import com.blazemaple.forum.util.HostHolder;
import com.blazemaple.forum.util.JsonUtils;
import com.blazemaple.forum.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

import static com.blazemaple.forum.common.ForumConstant.ENTITY_TYPE_COMMENT;

/**
 * @description
 *
 * @author BlazeMaple
 * @date 2023/6/27 14:46
 */

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController implements ForumConstant {

    private final CommentService commentService;

    private final HostHolder hostHolder;

    private final DiscussPostService discussPostService;

    private final RabbitMQProducer rabbitMQProducer;

    private final RedisTemplate redisTemplate;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.insertComment(comment);
        Event event = new Event()
            .setTopic(TOPIC_COMMENT)
            .setUserId(hostHolder.getUser().getId())
            .setEntityType(comment.getEntityType())
            .setEntityId(comment.getEntityId())
            .setData("postId", discussPostId);
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.selectDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.getById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        rabbitMQProducer.fireMessageEvent(event,RabbitMQConfig.EXCHANGE_MSG,"sys.msg."+TOPIC_COMMENT);

        if (comment.getEntityType()==ENTITY_TYPE_POST){
            event=new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(discussPostId);
            rabbitMQProducer.fireMessageEvent(event, RabbitMQConfig.EXCHANGE_PUBLISH,"publish."+PUBLISH_COMMENt);
            //计算帖子分数
            String redisKey= RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey,discussPostId);
        }
        return "redirect:/discuss/detail/" + discussPostId;
    }
}
