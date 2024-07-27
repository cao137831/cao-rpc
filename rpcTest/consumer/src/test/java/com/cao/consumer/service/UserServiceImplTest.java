package com.cao.consumer.service;

import com.cao.common.model.User;
import com.cao.common.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
public class UserServiceImplTest {

    @Resource
    private UserServiceImpl userService;

    @Test
    public void login() {
        User user = new User();
        user.setUsername("cao1234");
        user.setPassword("123456789");
        user.setCheckPassword("12345678");
        boolean login = userService.login(user);
        if (login) {
            System.out.println("登陆成功");
        } else {
            System.out.println("登陆失败");
        }
    }
}