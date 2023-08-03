package com.blazemaple.forum.mapper;

import com.blazemaple.forum.domain.entity.DiscussPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author BlazeMaple
* @description 针对表【discuss_post】的数据库操作Mapper
* @createDate 2023-06-18 16:51:10
* @Entity com.blazemaple.forum.domain.entity.DiscussPost
*/
@Mapper
public interface DiscussPostMapper extends BaseMapper<DiscussPost> {

    public List<DiscussPost> selectDiscussPost(Integer userId,Integer offset,Integer limit,Integer orderMode);

    public Integer selectDiscussPostRows(Integer userId);

    public Integer updateCommentCount(Integer id,Integer commentCount);

}




