package com.cao.examplecommon.service;

import com.cao.examplecommon.model.User;

/**
 * 用户服务接口
 */
public interface UserService {
    /**
     * 获取用户
     *
     * @param user
     * @return
     */
    User getUser(User user);


    String getUrl();

    /**
     * 获取数字
     *
     * @return
     */
    default short getNumber() {
        return 1;
    }
}
