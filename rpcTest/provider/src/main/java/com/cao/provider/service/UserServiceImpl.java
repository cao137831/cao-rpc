package com.cao.provider.service;

import com.cao.common.model.User;
import com.cao.common.service.UserService;
import com.cao.rpcspringbootstarter.annotation.RpcService;
import org.springframework.stereotype.Service;

@Service
@RpcService
public class UserServiceImpl implements UserService {
    @Override
    public boolean login(User user) {
        System.out.println("当前请求用户为：" + user.getUsername());
        return user.getPassword().equals(user.getCheckPassword());
    }
}
