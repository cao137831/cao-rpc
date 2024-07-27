package com.cao.rpccore.fault.retry;

import com.cao.rpccore.registry.EtcdRegistry;
import com.cao.rpccore.registry.Registry;
import com.cao.rpccore.spi.SpiLoader;

/**
 * 重试策略工厂
 */
public class RetryStrategyFactory {

    static {
        SpiLoader.load(RetryStrategy.class);
    }

    /**
     * 默认重试策略
     */
    public static final RetryStrategy DEFAULT_RETRYSTRATEGY = new FixedIntervalRetryStrategy();

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static RetryStrategy getInstance(String key) {
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }
}
