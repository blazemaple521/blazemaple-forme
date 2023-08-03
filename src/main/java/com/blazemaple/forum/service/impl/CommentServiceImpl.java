package com.blazemaple.forum.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blazemaple.forum.domain.entity.Comment;
import com.blazemaple.forum.service.CommentService;
import com.blazemaple.forum.mapper.CommentMapper;
import com.blazemaple.forum.service.DiscussPostService;
import com.blazemaple.forum.util.SensitiveFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

import static com.blazemaple.forum.common.ForumConstant.ENTITY_TYPE_POST;

/**
* @author BlazeMaple
* @description 针对表【comment】的数据库操作Service实现
* @createDate 2023-06-18 16:51:00
*/
@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

    private final CommentMapper commentMapper;

    private final SensitiveFilter sensitiveFilter;

    private final DiscussPostService discussPostService;
    @Override
    public Integer selectCountByEntity(Integer entityType, Integer entityId) {
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

    @Override
    public List<Comment> selectCommentByEntity(Integer entityType, Integer entityId, Integer offset, Integer limit) {
        return commentMapper.selectCommentByEntity(entityType,entityId,offset,limit);
    }

    @Override
    public Integer insertComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        // 添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insert(comment);

        // 更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }

        return rows;
    }
}




