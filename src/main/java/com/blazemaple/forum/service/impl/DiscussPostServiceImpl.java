package com.blazemaple.forum.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blazemaple.forum.domain.entity.DiscussPost;
import com.blazemaple.forum.service.DiscussPostService;
import com.blazemaple.forum.mapper.DiscussPostMapper;
import com.blazemaple.forum.util.SensitiveFilter;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* @author BlazeMaple
* @description 针对表【discuss_post】的数据库操作Service实现
* @createDate 2023-06-18 16:51:10
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class DiscussPostServiceImpl extends ServiceImpl<DiscussPostMapper, DiscussPost>
    implements DiscussPostService{

    private final DiscussPostMapper discussPostMapper;

    private final SensitiveFilter sensitiveFilter;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    //帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    //帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;

    @PostConstruct
    public void init(){
        postListCache= Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                        if(key == null || key.length() == 0){
                            throw new IllegalArgumentException("参数错误!");
                        }
                        String[] params = key.split(":");
                        if(params == null || params.length != 2){
                            throw new IllegalArgumentException("参数错误!");
                        }
                        Integer offset = Integer.valueOf(params[0]);
                        Integer limit = Integer.valueOf(params[1]);
                        //二级缓存：Redis -> mysql
                        log.info("load post list from DB.");
                        return discussPostMapper.selectDiscussPost(0,offset,limit,1);
                    }
                });
        postRowsCache= Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {
                        log.info("load post rows from DB.");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }

    @Override
    public List<DiscussPost> selectDiscussPost(Integer userId, Integer offset, Integer limit,Integer orderMode) {
        if (userId == 0 && orderMode == 1) {
            return postListCache.get(offset + ":" + limit);
        }
        log.info("load post list from DB.");
        return discussPostMapper.selectDiscussPost(userId,offset,limit,orderMode);
    }

    @Override
    public Integer selectDiscussPostRows(Integer userId) {
        if (userId == 0) {
            return postRowsCache.get(userId);
        }
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Override
    public Integer insertDiscussPost(DiscussPost discussPost) {
        if (discussPost == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        // 转义HTML标记
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        // 过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));
        Integer result = discussPostMapper.insert(discussPost);
        return result;
    }

    @Override
    public DiscussPost selectDiscussPostById(Integer id) {
        return this.getById(id);
    }

    @Override
    public Integer updateCommentCount(Integer id, Integer commentCount) {
        return discussPostMapper.updateCommentCount(id,commentCount);
    }

    @Override
    public Integer updateType(Integer id, Integer type) {
        DiscussPost discussPost = this.getById(id);
        discussPost.setType(type);
        return discussPostMapper.updateById(discussPost);
    }

    @Override
    public Integer updateStatus(Integer id, Integer status) {
        DiscussPost discussPost = this.getById(id);
        discussPost.setStatus(status);
        return discussPostMapper.updateById(discussPost);
    }

    @Override
    public int updateScore(int id, double score) {
        DiscussPost discussPost = this.getById(id);
        discussPost.setScore(score);
        return discussPostMapper.updateById(discussPost);
    }
}




