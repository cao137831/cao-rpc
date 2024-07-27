package com.cao.exampleprovider;

import com.cao.examplecommon.model.User;
import com.cao.examplecommon.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名" + user.getName());
        return user;
    }

    @Override
    public String getUrl() {
        return "https://github.com/cao137831";
    }
}
