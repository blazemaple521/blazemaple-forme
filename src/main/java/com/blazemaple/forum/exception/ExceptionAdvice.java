package com.blazemaple.forum.exception;

import com.blazemaple.forum.util.ForumUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @description 统一处理异常的Controller配置类
 *
 * @author BlazeMaple
 * @date 2023/6/28 17:44
 */

@ControllerAdvice(annotations = Controller.class)
@Slf4j
public class ExceptionAdvice {


    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("服务器发生异常: " + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            log.error(element.toString());
        }

        String xRequestedWith = request.getHeader("x-requested-with");
        //处理异步请求的方式
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            response.setContentType("application/plain;charset=utf-8");//普通文本，浏览器会自动转换为json格式
            PrintWriter writer = response.getWriter();
            writer.write(ForumUtil.getJSONString(1, "服务器异常!"));
        } else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}