package com.blazemaple.forum.quartz;

import com.blazemaple.forum.common.ForumConstant;
import com.blazemaple.forum.domain.entity.DiscussPost;
import com.blazemaple.forum.service.DiscussPostService;
import com.blazemaple.forum.service.ElasticSearchService;
import com.blazemaple.forum.service.LikeService;
import com.blazemaple.forum.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author BlazeMaple
 * @description
 * @date 2023/7/10 17:36
 */
@Slf4j
@RequiredArgsConstructor
public class PostScoreRefreshJob implements Job, ForumConstant {

    private final RedisTemplate redisTemplate;

    private final LikeService likeService;

    private final DiscussPostService discussPostService;

    private final ElasticSearchService elasticSearchService;

    // 论坛纪元
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2023-05-21 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化论坛纪元失败!", e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if (operations == null) {
            log.info("[任务取消] 没有要刷新的帖子!");
            return;
        }

        log.info("[任务开始] 正在刷新帖子分数, 任务数: ", operations.size());
        while (operations.size() > 0) {
            refresh((Integer) operations.pop());
        }
        log.info("[任务结束] 帖子分数刷新完毕!");
    }


    private void refresh(int postId) {
        DiscussPost post = discussPostService.selectDiscussPostById(postId);
        if (post == null) {
            log.info("该帖子不存在:id=" + postId);
            return;
        }

        // 是否精华
        boolean wonderful = post.getStatus() == 1;
        // 评论数量
        int commentCount = post.getCommentCount();
        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        // 计算权重
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        // 分数=帖子权重+距离天数
        // w可能小于1，因为log存在，所以送入log的最小值应该为1
        // getTime()单位为ms
        double score = Math.log10(Math.max(1, w)) +
            (post.getCreateTime().getTime() - epoch.getTime()) / (3600 * 60 * 24);

        // 更新帖子分数
        discussPostService.updateScore(postId, score);
        // 更新elasticsearch
        post.setScore(score);
        elasticSearchService.savaDiscussPost(post);
    }
}
