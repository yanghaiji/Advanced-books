package com.javayh.advanced.limiter;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 本地缓存
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-12-03 5:28 PM
 */
public class LocalCacheMap {

    private final static ConcurrentHashMap<String, Long> LOCAL_CACHE_MAP
            = new ConcurrentHashMap<>(256);

    /**
     * 锁定
     *
     * @param key   key
     * @param value 值
     */
    public static void setCache(String key, Long value) {
        LOCAL_CACHE_MAP.put(key, value);
    }

    /**
     * 检验是否讯在
     *
     * @param key
     * @return
     */
    public static Long getCache(String key) {
        return LOCAL_CACHE_MAP.get(key);
    }

    /**
     * 删除cache
     *
     * @param key
     * @return
     */
    public static boolean remove(String key) {
        try {
            LOCAL_CACHE_MAP.remove(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
