package com.blazemaple.forum.service;

import com.blazemaple.forum.domain.entity.DiscussPost;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author BlazeMaple
* @description 针对表【discuss_post】的数据库操作Service
* @createDate 2023-06-18 16:51:10
*/
public interface DiscussPostService extends IService<DiscussPost> {

    public List<DiscussPost> selectDiscussPost(Integer userId,Integer offset,Integer limit,Integer orderMode);

    public Integer selectDiscussPostRows(Integer userId);

    public Integer insertDiscussPost(DiscussPost discussPost);

    public DiscussPost selectDiscussPostById(Integer id);

    public Integer updateCommentCount(Integer id,Integer commentCount);

    public Integer updateType(Integer id,Integer type);

    public Integer updateStatus(Integer id,Integer status);

    public int updateScore(int id, double score);

}
