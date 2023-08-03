package com.blazemaple.forum.util;


import com.blazemaple.forum.domain.entity.User;
import org.springframework.stereotype.Component;

/**
 * @description 持有用户信息,用于代替session对象.
 *
 * @author BlazeMaple
 * @date 2023/6/20 15:59
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }

}
