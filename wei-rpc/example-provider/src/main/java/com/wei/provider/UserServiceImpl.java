package com.wei.provider;

import com.wei.common.model.User;
import com.wei.common.service.UserService;

/**
 * 用户服务实现类
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println(user.getName());
        return user;
    }
}
