package com.cao.rpccore.bootstrap;

import com.cao.rpccore.RpcApplication;

/**
 * 服务消费者启动类（初始化）
 */
public class ConsumerBootStrap {
    /**
     * 初始化
     */
    public static void init() {
        /**
         * RPC 框架初始化（配置和注册中心）
         */
        RpcApplication.init();
    }
}
