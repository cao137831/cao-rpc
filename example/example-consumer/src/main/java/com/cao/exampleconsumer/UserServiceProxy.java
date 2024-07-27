package com.cao.exampleconsumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.cao.examplecommon.model.User;
import com.cao.examplecommon.service.UserService;
import com.cao.rpccore.model.RpcRequest;
import com.cao.rpccore.model.RpcResponse;
import com.cao.rpccore.serializer.JdkSerializer;
import com.cao.rpccore.serializer.Serializer;

import java.io.IOException;

/**
 * 用户服务静态代理
 */
public class UserServiceProxy implements UserService {
    @Override
    public User getUser(User user) {
        // 指定序列化器
        final Serializer serializer = new JdkSerializer();

        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder().serviceName(UserService.class.getName()).methodName("getUser").parameterTypes(new Class[]{User.class}).args(new Object[]{user}).build();
        try {
            // 序列化（Java 对象 => 字节数组）
            byte[] bodyBytes = serializer.serialize(rpcRequest);

            // 发送请求
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:8080").body(bodyBytes).execute()) {
                byte[] result = httpResponse.bodyBytes();
                // 反序列化（字节数组 => Java 对象）
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return (User) rpcResponse.getData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String getUrl() {
        return null;
    }
}
