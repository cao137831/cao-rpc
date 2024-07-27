package com.cao.common.service;

import com.cao.common.model.User;

/**
 * 用户接口
 */
public interface UserService {

    /**
     * 检查登录信息
     *
     * @param user 用户信息
     * @return 是否验证成功
     */
    boolean login(User user);
}
