package com.cao.rpccore.registry;

import com.cao.rpccore.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心服务本地缓存
 */
public class RegistryServiceCache {

    /**
     * 服务缓存
     */
    private List<ServiceMetaInfo> serviceMetaCache;

    /**
     * 写缓存
     *
     * @param serviceMetaCache
     */
    public void write(List<ServiceMetaInfo> serviceMetaCache) {
        this.serviceMetaCache = serviceMetaCache;
    }

    /**
     * 读缓存
     *
     * @return
     */
    public List<ServiceMetaInfo> read() {
        return serviceMetaCache;
    }

    /**
     * 清空缓存
     */
    public void clear() {
        serviceMetaCache = null;
    }
}
