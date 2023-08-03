package com.blazemaple.forum.service;

import com.blazemaple.forum.domain.entity.LoginTicket;
import com.blazemaple.forum.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

/**
 * @author BlazeMaple
 * @description 针对表【user】的数据库操作Service
 * @createDate 2023-06-18 16:51:29
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户id查询用户
     * @param userId
     * @return
     */
    User findUserById(Integer userId);

    /**
     * 注册用户
     * @param user
     * @return
     */
    Map<String, Object> register(User user);

    /**
     * 激活用户
     * @param userId
     * @param code
     * @return
     */
    Integer activation(Integer userId, String code);

    /**
     * 登录
     * @param username
     * @param password
     * @param expiredSeconds
     * @return
     */
    Map<String, Object> login(String username, String password, int expiredSeconds);


    /**
     * 退出登录
     * @param ticket
     */
    void logout(String ticket);

    /**
     * 根据ticket查询登录凭证
     * @param ticket
     * @return
     */
    LoginTicket findLoginTicket(String ticket);

    /**
     * 更新用户头像
     * @param userId
     * @param headerUrl
     * @return
     */
    Integer updateHeader(Integer userId, String headerUrl);

    /**
     * 根据用户名查询用户
     * @param toName
     * @return
     */
    User findUserByName(String toName);

//    User getUserFromRedis(int userId);
//    User initUserCache(int userId);

    /**
     * 清除用户缓存
     * @param userId
     */
    void clearUserCache(int userId);

    /**
     * 根据用户id查询用户权限
     * @param userId
     * @return
     */
    Collection<? extends GrantedAuthority> getAuthorities(int userId);


}
