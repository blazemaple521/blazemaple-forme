package com.blazemaple.forum.service;

import com.blazemaple.forum.domain.entity.Msg;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author BlazeMaple
* @description 针对表【message】的数据库操作Service
* @createDate 2023-06-18 16:51:25
*/
public interface MessageService extends IService<Msg> {

    public List<Msg> selectConversations(Integer userId, Integer offset, Integer limit);

    public Integer selectConversationCount(Integer userId);

    public List<Msg> selectLetters(String conversationId, Integer offset, Integer limit);

    public Integer selectLetterCount(String conversationId);

    public Integer selectLetterUnreadCount(Integer userId, String conversationId);

    public Integer readMessage(List<Integer> ids);

    public Integer insertMessage(Msg msg);

    public Msg selectLatestNotice(Integer userId, String topic);

    public int selectNoticeCount(Integer userId, String topic);

    public int selectNoticeUnreadCount(Integer userId, String topic);

    public List<Msg> selectNotices(Integer userId, String topic, Integer offset, Integer limit);
}
