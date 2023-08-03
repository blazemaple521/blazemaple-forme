package com.blazemaple.forum.es;

import com.blazemaple.forum.domain.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author BlazeMaple
 * @description
 * @date 2023/7/3 17:03
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {

}
