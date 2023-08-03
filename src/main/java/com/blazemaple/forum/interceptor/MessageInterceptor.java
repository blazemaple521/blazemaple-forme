package com.blazemaple.forum.interceptor;


import com.blazemaple.forum.domain.entity.User;
import com.blazemaple.forum.service.MessageService;
import com.blazemaple.forum.util.HostHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description 消息拦截器，为了在首页显示未读消息数量
 *
 * @author BlazeMaple
 * @date 2023/7/3 10:07
 */

@Component
@RequiredArgsConstructor
public class MessageInterceptor implements HandlerInterceptor {

    private final HostHolder hostHolder;

    private final MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            int noticeUnreadCount = messageService.selectNoticeUnreadCount(user.getId(), null);
            int letterUnreadCount = messageService.selectLetterUnreadCount(user.getId(), null);
            modelAndView.addObject("allUnreadCount", noticeUnreadCount + letterUnreadCount);
        }
    }
}
