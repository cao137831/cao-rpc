package com.cao.rpccore.bootstrap;

import com.cao.rpccore.RpcApplication;
import com.cao.rpccore.config.RegistryConfig;
import com.cao.rpccore.config.RpcConfig;
import com.cao.rpccore.model.ServiceMetaInfo;
import com.cao.rpccore.model.ServiceRegisterInfo;
import com.cao.rpccore.registry.LocalRegistry;
import com.cao.rpccore.registry.Registry;
import com.cao.rpccore.registry.RegistryFactory;
import com.cao.rpccore.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * 服务提供停车启动类（初始化）
 */
public class ProviderBootStrap {
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        // RPC 框架初始化（配置和注册中心）
        RpcApplication.init();
        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            // 本地注册
            LocalRegistry.registry(serviceName, serviceRegisterInfo.getImplClass());

            // 注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " 服务注册失败", e);
            }
        }
        // 启动服务器
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }
}
