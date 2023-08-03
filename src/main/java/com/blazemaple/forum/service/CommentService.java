package com.blazemaple.forum.service;

import com.blazemaple.forum.domain.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author BlazeMaple
* @description 针对表【comment】的数据库操作Service
* @createDate 2023-06-18 16:51:00
*/
public interface CommentService extends IService<Comment> {

    public Integer selectCountByEntity(Integer entityType, Integer entityId);

    public List<Comment> selectCommentByEntity(Integer entityType, Integer entityId, Integer offset, Integer limit);

    public Integer insertComment(Comment comment);

}
