package com.blazemaple.forum.mapper;

import com.blazemaple.forum.domain.entity.Msg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author BlazeMaple
 * @description 针对表【message】的数据库操作Mapper
 * @createDate 2023-06-18 16:51:25
 * @Entity com.blazemaple.forum.domain.entity.Msg
 */
@Mapper
public interface MessageMapper extends BaseMapper<Msg> {

    public List<Msg> selectConversations(Integer userId, Integer offset, Integer limit);

    public Integer selectConversationCount(Integer userId);

    public List<Msg> selectLetters(String conversationId, Integer offset, Integer limit);

    public Integer selectLetterCount(String conversationId);

    public Integer selectLetterUnreadCount(Integer userId, String conversationId);

    public Integer updateStatus(List<Integer> ids, Integer status);

    public Msg selectLatestNotice(Integer userId, String topic);

    public Integer selectNoticeCount(Integer userId, String topic);

    public Integer selectNoticeUnreadCount(Integer userId, String topic);

    public List<Msg> selectNotices(Integer userId, String topic, Integer offset, Integer limit);


}




