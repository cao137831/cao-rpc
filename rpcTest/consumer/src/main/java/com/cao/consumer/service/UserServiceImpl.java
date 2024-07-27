package com.cao.consumer.service;

import com.cao.common.model.User;
import com.cao.common.service.UserService;
import com.cao.rpcspringbootstarter.annotation.RpcReference;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @RpcReference
    private UserService userService;

    @Override
    public boolean login(User user) {
        return userService.login(user);
    }
}
