package com.cao.common.model;

import lombok.Data;

/**
 * 用户信息
 */
@Data
public class User {

    private String username;

    private String password;

    private String checkPassword;

}
