package com.cao.rpccore.fault.tolerant;

import com.cao.rpccore.fault.retry.FixedIntervalRetryStrategy;
import com.cao.rpccore.fault.retry.RetryStrategy;
import com.cao.rpccore.spi.SpiLoader;

/**
 * 容错机制策略工厂
 */
public class TolerantStrategyFactory {

    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    /**
     * 默认重试策略
     */
    public static final TolerantStrategy DEFAULT_TOLERANTSTRATEGY = new FailFastTolerantStrategy();

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static TolerantStrategy getInstance(String key) {
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }
}
