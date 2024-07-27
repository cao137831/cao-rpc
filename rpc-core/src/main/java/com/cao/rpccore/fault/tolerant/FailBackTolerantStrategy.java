package com.cao.rpccore.fault.tolerant;

import com.cao.rpccore.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 失败自动恢复 -> 降级 - 容错策略
 */
@Slf4j
public class FailBackTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // todo 降级到其他服务 的实现
        return null;
    }
}
