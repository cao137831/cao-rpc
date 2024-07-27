package com.cao.exampleconsumer;

import com.cao.examplecommon.model.User;
import com.cao.examplecommon.service.UserService;
import com.cao.rpccore.RpcApplication;
import com.cao.rpccore.bootstrap.ConsumerBootStrap;
import com.cao.rpccore.config.RpcConfig;
import com.cao.rpccore.proxy.ServiceProxyFactory;

/**
 * 服务消费者示例
 */
public class ConsumerExample {
    public static void main(String[] args) {
        ConsumerBootStrap.init();
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        UserService userService = null;
        if (rpcConfig.isMock()) {
            userService = ServiceProxyFactory.getMockProxy(UserService.class);
        } else {
            userService = ServiceProxyFactory.getProxy(UserService.class);
        }

        String url = userService.getUrl();
        System.out.println(url);

        User user = new User();
        user.setName("caohaolong");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user is null");
        }
    }
}
