package com.cao.rpccore.fault.tolerant;

import com.cao.rpccore.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 故障转移 - 容错策略
 */
@Slf4j
public class FailOverTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // todo 转移到其他服务节点 的实现
        return null;
    }
}
