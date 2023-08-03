package com.blazemaple.forum.controller;

import com.blazemaple.forum.common.ForumConstant;
import com.blazemaple.forum.domain.entity.DiscussPost;
import com.blazemaple.forum.domain.entity.Page;
import com.blazemaple.forum.service.ElasticSearchService;
import com.blazemaple.forum.service.LikeService;
import com.blazemaple.forum.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BlazeMaple
 * @description
 * @date 2023/7/4 15:55
 */
@Controller
@RequiredArgsConstructor
public class SearchController implements ForumConstant {

    private final ElasticSearchService elasticSearchService;

    private final UserService userService;

    private final LikeService likeService;

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) {
        // 搜索帖子
        org.springframework.data.domain.Page<DiscussPost> searchResults = elasticSearchService.searchDiscussPost(
            keyword, page.getCurrent() - 1, page.getLimit());

        // 聚合数据
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (searchResults != null) {
            for (DiscussPost post : searchResults) {
                Map<String, Object> map = new HashMap<>();

                // 帖子
                map.put("post", post);
                // 作者
                map.put("user", userService.findUserById(post.getUserId()));
                // 点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        page.setPath("search?keyword=" + keyword);
        page.setRows(searchResults == null ? 0 : (int) searchResults.getTotalElements());

        return "/site/search";
    }
}
