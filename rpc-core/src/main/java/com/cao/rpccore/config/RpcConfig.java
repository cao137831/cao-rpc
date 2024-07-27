package com.cao.rpccore.config;

import com.cao.rpccore.fault.retry.RetryStrategyKeys;
import com.cao.rpccore.fault.tolerant.TolerantStrategyKeys;
import com.cao.rpccore.loadbalancer.LoadBalancer;
import com.cao.rpccore.loadbalancer.LoadBalancerKeys;
import com.cao.rpccore.serializer.Serializer;
import com.cao.rpccore.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC 框架配置
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name = "cao";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口号
     */
    private Integer serverPort = 8080;

    /**
     * 模拟调用
     */
    private boolean mock = false;

    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 负载均衡器
     */
    private String loadBalancerKey = LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     */
    private String retryStrategyKey = RetryStrategyKeys.FIXED_INTERVAL;

    /**
     * 容错策略
     */
    private String tolerantStrategyKey = TolerantStrategyKeys.FAIL_FAST;
}
