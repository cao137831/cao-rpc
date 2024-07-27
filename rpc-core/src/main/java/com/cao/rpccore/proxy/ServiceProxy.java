package com.cao.rpccore.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.cao.rpccore.RpcApplication;
import com.cao.rpccore.config.RpcConfig;
import com.cao.rpccore.constant.RpcConstant;
import com.cao.rpccore.fault.retry.RetryStrategy;
import com.cao.rpccore.fault.retry.RetryStrategyFactory;
import com.cao.rpccore.fault.tolerant.TolerantStrategy;
import com.cao.rpccore.fault.tolerant.TolerantStrategyFactory;
import com.cao.rpccore.loadbalancer.LoadBalancer;
import com.cao.rpccore.loadbalancer.LoadBalancerFactory;
import com.cao.rpccore.model.ServiceMetaInfo;
import com.cao.rpccore.protocol.ProtocolConstant;
import com.cao.rpccore.protocol.ProtocolMessage;
import com.cao.rpccore.protocol.ProtocolMessageDecoder;
import com.cao.rpccore.protocol.ProtocolMessageEncoder;
import com.cao.rpccore.protocol.enums.ProtocolMessageSerializerEnum;
import com.cao.rpccore.protocol.enums.ProtocolMessageTypeEnum;
import com.cao.rpccore.registry.Registry;
import com.cao.rpccore.registry.RegistryFactory;
import com.cao.rpccore.serializer.JdkSerializer;
import com.cao.rpccore.serializer.Serializer;
import com.cao.rpccore.model.RpcRequest;
import com.cao.rpccore.model.RpcResponse;
import com.cao.rpccore.serializer.SerializerFactory;
import com.cao.rpccore.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 服务代理（使用 JDK 动态代理） -- 通过 TCP 请求实现远程调用
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {
    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder().serviceName(serviceName).methodName(method.getName()).parameterTypes(method.getParameterTypes()).args(args).build();

        // 序列化
        byte[] bodyBytes = serializer.serialize(rpcRequest);

        // 从注册中心获取服务提供者请求地址
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);

        RpcResponse rpcResponse = null;
        try {
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("暂无服务地址");
            }

            // 暂时先获取第一个 todo 暂时先获取第一个，待优化
//            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);

            // 负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancerKey());

            // 将方法名（请求路径）作为负载均衡参数
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);


            // 重试机制
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategyKey());
            rpcResponse = retryStrategy.doRetry(() -> {
                // 发送 TCP 请求
                return VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo);
            });
        } catch (IOException e) {
            // 容错机制
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategyKey());
            rpcResponse = tolerantStrategy.doTolerant(null, e);
        }
        return rpcResponse.getData();
    }
}
