package com.blazemaple.forum.service;

/**
 * @author BlazeMaple
 * @description
 * @date 2023/6/29 15:25
 */

public interface LikeService {

    public void like(Integer userId, Integer entityType, Integer entityId, Integer entityUserId);

    public Long findEntityLikeCount(Integer entityType, Integer entityId);

    public Integer findEntityLikeStatus(Integer userId, Integer entityType, Integer entityId);

    public Integer findUserLikeCount(Integer userId);

}
