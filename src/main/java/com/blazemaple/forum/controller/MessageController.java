package com.blazemaple.forum.controller;

import com.blazemaple.forum.common.ForumConstant;
import com.blazemaple.forum.domain.entity.Msg;
import com.blazemaple.forum.domain.entity.Page;
import com.blazemaple.forum.domain.entity.User;
import com.blazemaple.forum.service.MessageService;
import com.blazemaple.forum.service.UserService;
import com.blazemaple.forum.util.ForumUtil;
import com.blazemaple.forum.util.HostHolder;
import com.blazemaple.forum.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author BlazeMaple
 * @description
 * @date 2023/6/27 16:23
 */
@Controller
@RequiredArgsConstructor
public class MessageController implements ForumConstant {

    private final MessageService messageService;

    private final HostHolder hostHolder;

    private final UserService userService;

    // 私信列表
    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.selectConversationCount(user.getId()));

        // 会话列表
        List<Msg> conversationList = messageService.selectConversations(
            user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Msg msg : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", msg);
                map.put("letterCount", messageService.selectLetterCount(msg.getConversationId()));
                map.put("unreadCount",
                    messageService.selectLetterUnreadCount(user.getId(), msg.getConversationId()));
                int targetId = user.getId() == msg.getFromId() ? msg.getToId() : msg.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        // 查询未读消息数量
        int letterUnreadCount = messageService.selectLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        // 查询未读通知数量
        int noticeUnreadCount = messageService.selectNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.selectLetterCount(conversationId));

        // 私信列表
        List<Msg> letterList = messageService.selectLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Msg msg : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", msg);
                map.put("fromUser", userService.findUserById(msg.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);

        // 私信目标
        model.addAttribute("target", getLetterTarget(conversationId));

        // 设置已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User target = userService.findUserByName(toName);
        if (target == null) {
            return ForumUtil.getJSONString(1, "目标用户不存在！");
        }

        Msg msg = new Msg();
        msg.setFromId(hostHolder.getUser().getId());
        msg.setToId(target.getId());
        if (msg.getFromId() < msg.getToId()) {
            msg.setConversationId(msg.getFromId() + "_" + msg.getToId());
        } else {
            msg.setConversationId(msg.getToId() + "_" + msg.getFromId());
        }
        msg.setContent(content);
        msg.setCreateTime(new Date());
        messageService.insertMessage(msg);

        return ForumUtil.getJSONString(0);
    }

    @RequestMapping(path="/notice/list", method = RequestMethod.GET)
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();

        // 查询评论类通知
        Msg msg = messageService.selectLatestNotice(user.getId(), TOPIC_COMMENT);
        Map<String, Object> messageVO = new HashMap<>();
        messageVO.put("msg", msg);
        if (msg != null) {
            String content = HtmlUtils.htmlUnescape(msg.getContent());
            HashMap<String,Object> data = JsonUtils.jsonToPojo(content, HashMap.class);
            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));
            int noticeCount = messageService.selectNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("count", noticeCount);
            int unreadCount = messageService.selectNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
            messageVO.put("unread", 0);
        }
        model.addAttribute("commentNotice", messageVO);

        // 查询点赞类通知
        msg = messageService.selectLatestNotice(user.getId(), TOPIC_LIKE);
        messageVO = new HashMap<>();
        messageVO.put("msg", msg);
        if (msg != null) {
            String content = HtmlUtils.htmlUnescape(msg.getContent());
            HashMap<String,Object> data = JsonUtils.jsonToPojo(content, HashMap.class);
            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            messageVO.put("postId", data.get("postId"));
            int noticeCount = messageService.selectNoticeCount(user.getId(), TOPIC_LIKE);
            messageVO.put("count", noticeCount);
            int unreadCount = messageService.selectNoticeUnreadCount(user.getId(), TOPIC_LIKE);
            messageVO.put("unread", unreadCount);
        }
        model.addAttribute("likeNotice", messageVO);

        // 查询关注类通知
        msg = messageService.selectLatestNotice(user.getId(), TOPIC_FOLLOW);
        messageVO = new HashMap<>();
        messageVO.put("msg", msg);
        if (msg != null) {
            String content = HtmlUtils.htmlUnescape(msg.getContent());
            HashMap<String,Object> data = JsonUtils.jsonToPojo(content, HashMap.class);
            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            int noticeCount = messageService.selectNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("count", noticeCount);
            int unreadCount = messageService.selectNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
            messageVO.put("unread", unreadCount);
        }
        model.addAttribute("followNotice", messageVO);

        // 查询未读消息数量
        int letterUnreadCount = messageService.selectLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        // 查询未读通知数量
        int noticeUnreadCount = messageService.selectNoticeUnreadCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/notice";
    }

    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic,Page page,Model model){
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.selectNoticeCount(user.getId(), topic));

        List<Msg> noticeList = messageService.selectNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVOList = new ArrayList<>();
        if (noticeList != null) {
            for (Msg notice : noticeList) {
                Map<String, Object> map = new HashMap<>();
                // 通知
                map.put("notice", notice);
                // 内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                HashMap<String,Object> data = JsonUtils.jsonToPojo(content, HashMap.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                map.put("fromUser", userService.findUserById(notice.getFromId()));
                noticeVOList.add(map);
            }
        }
        model.addAttribute("notices", noticeVOList);
        // 设置已读
        List<Integer> ids = getLetterIds(noticeList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return "/site/notice-detail";
    }


    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    private List<Integer> getLetterIds(List<Msg> letterList) {
        List<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            for (Msg msg : letterList) {
                if (hostHolder.getUser().getId().equals(msg.getToId()) && msg.getStatus() == 0) {
                    ids.add(msg.getId());
                }
            }
        }

        return ids;
    }


}
