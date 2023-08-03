package com.blazemaple.forum.controller;

import com.blazemaple.forum.common.ForumConstant;
import com.blazemaple.forum.domain.entity.DiscussPost;
import com.blazemaple.forum.domain.entity.Page;
import com.blazemaple.forum.domain.entity.User;
import com.blazemaple.forum.service.DiscussPostService;
import com.blazemaple.forum.service.LikeService;
import com.blazemaple.forum.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BlazeMaple
 * @description
 * @date 2023/6/18 15:30
 */
@Controller
@RequiredArgsConstructor
public class HomeController implements ForumConstant {

    private final DiscussPostService discussPostService;

    private final UserService userService;

    private final LikeService likeService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page, @RequestParam(name = "orderMode",defaultValue = "0") int orderMode){
        page.setRows(discussPostService.selectDiscussPostRows(0));
        page.setPath("/index?orderMode="+orderMode);
        List<DiscussPost> list = discussPostService.selectDiscussPost(0, page.getOffset(), page.getLimit(),orderMode);
        List<Map<String,Object>> discussPosts=new ArrayList<>();
        if (list!=null){
            for (DiscussPost post : list) {
                Map<String,Object> map=new HashMap<>();
                map.put("post",post);
                User tempUser = userService.findUserById(post.getUserId());
                map.put("user",tempUser);
                Long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount",likeCount);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode",orderMode);
        return "/index";
    }


    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String getErrorPage() {
        return "/error/500";
    }

    //拒绝访问的提示页面
    @RequestMapping(path = {"/denied"}, method = {RequestMethod.GET})
    public String getDeniedPage() {
        return "/error/404";
    }


}
