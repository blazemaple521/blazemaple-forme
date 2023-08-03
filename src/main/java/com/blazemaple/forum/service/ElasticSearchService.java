package com.blazemaple.forum.service;

import com.blazemaple.forum.domain.entity.DiscussPost;
import org.springframework.data.domain.Page;

/**
 * @author BlazeMaple
 * @description
 * @date 2023/7/3 17:16
 */
public interface ElasticSearchService {

    public void savaDiscussPost(DiscussPost discussPost);

    public void deleteDiscussPost(int postId);

    public Page<DiscussPost> searchDiscussPost(String keyword,int current,int limit);

}
