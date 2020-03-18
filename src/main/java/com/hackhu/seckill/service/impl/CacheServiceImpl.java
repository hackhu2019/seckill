package com.hackhu.seckill.service.impl;

import com.google.common.cache.Cache;
import com.hackhu.seckill.service.CacheService;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author hackhu
 * @date 2020/3/18
 */
@Service
public class CacheServiceImpl implements CacheService {
    private Cache<String, Object> commonCache;

    @PostConstruct
    public void init() {
        commonCache = CacheBuilder.newBuilder()
                // 设置缓存容器初始化容量为 10
                .initialCapacity(10)
                // 设置缓存中最大可存储 key 数量为 100，超过上限按照 LRU 策略移除缓存
                .maximumSize(100)
                // 设置缓存过期时间为 60 秒
                .expireAfterWrite(60, TimeUnit.SECONDS).build();
    }
    @Override
    public void setCommonCache(String key, Object value) {
        commonCache.put(key, value);
    }

    @Override
    public Object getFromCommonCache(String key) {
        return commonCache.getIfPresent(key);
    }
}
