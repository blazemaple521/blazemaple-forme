package com.blazemaple.forum.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blazemaple.forum.domain.entity.Msg;
import com.blazemaple.forum.service.MessageService;
import com.blazemaple.forum.mapper.MessageMapper;
import com.blazemaple.forum.util.SensitiveFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author BlazeMaple
 * @description 针对表【message】的数据库操作Service实现
 * @createDate 2023-06-18 16:51:25
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Msg>
    implements MessageService {

    private final MessageMapper messageMapper;

    private final SensitiveFilter sensitiveFilter;

    @Override
    public List<Msg> selectConversations(Integer userId, Integer offset, Integer limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    @Override
    public Integer selectConversationCount(Integer userId) {
        return messageMapper.selectConversationCount(userId);
    }

    @Override
    public List<Msg> selectLetters(String conversationId, Integer offset, Integer limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    @Override
    public Integer selectLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    @Override
    public Integer selectLetterUnreadCount(Integer userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    @Override
    public Integer readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }

    @Override
    public Integer insertMessage(Msg msg) {
        msg.setContent(HtmlUtils.htmlEscape(msg.getContent()));
        msg.setContent(sensitiveFilter.filter(msg.getContent()));
        return messageMapper.insert(msg);
    }

    @Override
    public Msg selectLatestNotice(Integer userId, String topic) {
        return messageMapper.selectLatestNotice(userId, topic);
    }

    @Override
    public int selectNoticeCount(Integer userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }

    @Override
    public int selectNoticeUnreadCount(Integer userId, String topic) {
        return messageMapper.selectNoticeUnreadCount(userId, topic);
    }

    @Override
    public List<Msg> selectNotices(Integer userId, String topic, Integer offset, Integer limit) {
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }
}




