package com.javayh.advanced.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 * redis 工具
 * </p>
 * <br>
 *     参考 https://github.com/yanghaiji/javayh-platform/tree/master/javayh-dependencies/javayh-redis-starter/src/main/java/com/javayh/redis
 * </br>
 * @author Dylan
 * @version 1.0.0
 * @since 2020-12-17 4:24 PM
 */
@Slf4j
public class RedisUtil<K,V> {

    private RedisTemplate<K, V> redisTemplate;
    private StringRedisTemplate stringRedisTemplate;
    private HashOperations<K, K, V> hashOperations;
    private ListOperations<K,V> listOperations;
    private ZSetOperations<K,V> zSetOperations;
    private SetOperations<K,V> setOperations;
    private ValueOperations<K,V> valueOperations;

    public RedisUtil(RedisTemplate<K, V> redisTemplate,
                     StringRedisTemplate stringRedisTemplate,
                     HashOperations<K, K, V> hashOperations,
                     ListOperations<K, V> listOperations,
                     ZSetOperations<K, V> zSetOperations,
                     SetOperations<K, V> setOperations,
                     ValueOperations<K,V> valueOperations) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.hashOperations = hashOperations;
        this.listOperations = listOperations;
        this.zSetOperations = zSetOperations;
        this.setOperations  = setOperations;
        this.valueOperations  = valueOperations;
    }

    /**
     * <p>
     * 指定缓存失效时间
     * </p>
     * @version 1.0.0
     * @author Yang-haiji
     * @param key key
     * @param time 时间
     * @return boolean
     */
    public boolean expire(K key, long time) {
        checkKey(key);
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        }
        catch (Exception e) {
            log.error("指定缓存失效时间{}", e);
            return false;
        }
    }

    /**
     * <p>
     *       判断 是否存在
     * </p>
     * @version 1.0.0
     * @since 12/17/2020
     * @param key
     * @return boolean
     */
    public boolean exists(@NotNull K key) {
        checkKey(key);
        return Objects.isNull(key) ? false : redisTemplate.hasKey(key);
    }

    /**
     * String 类型写入
     * @param key   key
     * @param value value
     */
    public void addStr(K key,V value){
        checkKey(key);
        valueOperations.set(key, value);
    }

    /**
     * 返回value
     * @param key key
     * @return
     */
    public V getStr(K key){
        checkKey(key);
       return valueOperations.get(key);
    }

    public long incr(K key, long delta) {
        checkKey(key);
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return valueOperations.increment(key, delta);
    }

    public long decr(K key, long delta) {
        checkKey(key);
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return valueOperations.increment(key, -delta);
    }

    public boolean setHashTime(K key, Map<K, V> map, long time) {
        try {
            hashOperations.putAll(key, map);
            if (time > 0) {
                time = +getTime(getTime());
                expire(key, time);
            }
            return true;
        }
        catch (Exception e) {
            log.error("HashSet并设置时间存放入{},详细信息{}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<K, V> getHashAll(K key) {
        return hashOperations.entries(key);
    }


    public Long leftPush(K key,V value){
        checkKey(key);
        return listOperations.leftPush(key, value);
    }

    public Long leftPushAll(K key, Collection<V> value){
        checkKey(key);
        return listOperations.leftPushAll(key, value);
    }

    public Long rightPush(K key,V value){
        checkKey(key);
        return listOperations.rightPush(key, value);
    }

    public Long rightPush(K key, Collection<V> value){
        checkKey(key);
        return listOperations.rightPushAll(key, value);
    }



    private static long getTime() {
        return (long) ((Math.random() * 200) + 1);
    }

    /**
     * <p>
     * 获取随机数
     * </p>
     * @version 1.0.0
     * @author Dylan-haiji
     * @since 2020/3/5
     * @param num
     * @return long
     */
    private static long getTime(long num) {
        if (num == 0) {
            num = getTime();
        }
        num <<= 2;
        return num;
    }

    private void checkKey(K key){
        if(Objects.isNull(key)){
            throw new RedisKeyException("Redis key is null");
        }
    }


}
