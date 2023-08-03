package com.blazemaple.forum.interceptor;

import com.blazemaple.forum.domain.entity.User;
import com.blazemaple.forum.service.DataService;
import com.blazemaple.forum.util.HostHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author BlazeMaple
 * @description
 * @date 2023/7/10 14:42
 */
@Component
@RequiredArgsConstructor
public class DataInterceptor implements HandlerInterceptor {

    private final DataService dataService;

    private final HostHolder hostHolder;

    @Override
    public boolean preHandle(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);
        User user = hostHolder.getUser();
        if (user != null) {
            dataService.recordDAU(user.getId());
        }
        return true;
    }


}
