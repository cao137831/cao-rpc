package com.cao.rpccore.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.cao.rpccore.model.RpcRequest;
import com.cao.rpccore.model.RpcResponse;
import com.cao.rpccore.serializer.JdkSerializer;
import com.cao.rpccore.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Mock 服务代理
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {
    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 根据方法的返回值类型，生成特定的默认值对象
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());
        return getDefaultObject(methodReturnType);
    }

    private Object getDefaultObject(Class<?> methodReturnType) {
        // 基本类型
        if (methodReturnType.isPrimitive()) {
            if (methodReturnType == boolean.class) {
                return false;
            } else if (methodReturnType == short.class) {
                return (short) 0;
            } else if (methodReturnType == int.class) {
                return 0;
            } else if (methodReturnType == long.class) {
                return 0L;
            } else if (methodReturnType == byte.class) {
                return (byte) 0;
            } else if (methodReturnType == char.class) {
                return (char) 0;
            } else if (methodReturnType == float.class) {
                return 0f;
            } else if (methodReturnType == double.class) {
                return 0d;
            }
        }
        // 对象类型
        return null;
    }
}
