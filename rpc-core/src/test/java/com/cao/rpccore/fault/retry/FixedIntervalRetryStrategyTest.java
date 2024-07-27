package com.cao.rpccore.fault.retry;

import com.cao.rpccore.model.RpcResponse;
import org.junit.Test;

import static org.junit.Assert.*;

public class FixedIntervalRetryStrategyTest {


    @Test
    public void doRetry() {
        RetryStrategy noRetryStrategy = new NoRetryStrategy();
        try {
            RpcResponse rpcResponse = noRetryStrategy.doRetry(() -> {
                System.out.println("测试重试");
                throw new RuntimeException("重试失败");
            });
            System.out.println(rpcResponse);
        } catch (Exception e) {
            System.out.println("多次重试失败");
            e.printStackTrace();
        }
    }

    @Test
    public void doRetry2() {
        RetryStrategy fixedIntervalRetryStrategy = new FixedIntervalRetryStrategy();
        try {
            RpcResponse rpcResponse = fixedIntervalRetryStrategy.doRetry(() -> {
                System.out.println("测试重试");
                throw new RuntimeException("重试失败");
            });
            System.out.println(rpcResponse);
        } catch (Exception e) {
            System.out.println("多次重试失败");
            e.printStackTrace();
        }
    }
}