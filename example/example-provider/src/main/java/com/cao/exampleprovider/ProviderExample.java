package com.cao.exampleprovider;

import com.cao.examplecommon.service.UserService;
import com.cao.rpccore.RpcApplication;
import com.cao.rpccore.config.RegistryConfig;
import com.cao.rpccore.config.RpcConfig;
import com.cao.rpccore.model.ServiceMetaInfo;
import com.cao.rpccore.registry.LocalRegistry;
import com.cao.rpccore.registry.Registry;
import com.cao.rpccore.registry.RegistryFactory;
import com.cao.rpccore.server.tcp.VertxTcpServer;

/**
 * 服务提供者示例
 */
public class ProviderExample {
    public static void main(String[] args) {
        // rpc框架初始化，主要工作是加载rpc配置，并确定注册中心的使用
        RpcApplication.init();

        // 全局 rpc 配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.registry(serviceName, UserServiceImpl.class);

        // 注册服务搭配注册中心
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 启动 web 服务
//        HttpServer httpServer = new VertxHttpSever();
//        httpServer.doStart(rpcConfig.getServerPort());

        // 启动 TCP 服务
        VertxTcpServer tcpServer = new VertxTcpServer();
        tcpServer.doStart(rpcConfig.getServerPort());

    }
}
