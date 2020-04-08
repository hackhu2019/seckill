package com.hackhu.seckill.service;

/**
 * @author hackhu
 * @date 2020/3/18
 */
public interface CacheService {
    /**
     * 将数据存入缓存
     * @param key
     * @param value
     */
    void setCommonCache(String key, Object value);

    /**
     * 读取缓存
     * @param key
     * @return
     */
    Object getFromCommonCache(String key);
}
