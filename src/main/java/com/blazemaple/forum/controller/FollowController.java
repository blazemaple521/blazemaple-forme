package com.blazemaple.forum.controller;

import com.blazemaple.forum.annotation.LoginRequired;
import com.blazemaple.forum.common.ForumConstant;
import com.blazemaple.forum.config.RabbitMQConfig;
import com.blazemaple.forum.domain.entity.Event;
import com.blazemaple.forum.domain.entity.Page;
import com.blazemaple.forum.domain.entity.User;
import com.blazemaple.forum.event.RabbitMQProducer;
import com.blazemaple.forum.service.FollowService;
import com.blazemaple.forum.service.UserService;
import com.blazemaple.forum.util.ForumUtil;
import com.blazemaple.forum.util.HostHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

import static com.blazemaple.forum.common.ForumConstant.ENTITY_TYPE_USER;

/**
 * @author BlazeMaple
 * @description
 * @date 2023/7/1 10:16
 */
@Controller
@RequiredArgsConstructor
public class FollowController implements ForumConstant {

    private final FollowService followService;

    private final HostHolder hostHolder;

    private final UserService userService;

    private final RabbitMQProducer rabbitMQProducer;

    @RequestMapping(path = "/follow",method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);
        Event event=new Event()
            .setTopic(TOPIC_FOLLOW)
            .setUserId(hostHolder.getUser().getId())
            .setEntityType(entityType)
            .setEntityId(entityId)
            .setEntityUserId(entityId);
        rabbitMQProducer.fireMessageEvent(event, RabbitMQConfig.EXCHANGE_MSG,"sys.msg."+TOPIC_FOLLOW);
        return ForumUtil.getJSONString(0, "已关注!");
    }

    @RequestMapping(path = "/unfollow",method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(), entityType, entityId);
        return ForumUtil.getJSONString(0, "已取消关注!");
    }

    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

        List<Map<String, Object>> userList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "/site/followee";
    }

    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "/site/follower";
    }

    private boolean hasFollowed(int userId) {
        if (hostHolder.getUser() == null) {
            return false;
        }

        return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }



}
