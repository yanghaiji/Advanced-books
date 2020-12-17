package com.javayh.advanced.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * <p>
 * redis 配置
 * </p>
 * <br>
 *     参考 https://github.com/yanghaiji/javayh-platform/tree/master/javayh-dependencies/javayh-redis-starter/src/main/java/com/javayh/redis
 * </br>
 * @author Dylan
 * @version 1.0.0
 * @since 2020-12-17 3:49 PM
 */
@Configuration
public class RedisConfiguration {

    private final LettuceConnectionFactory lettuceConnectionFactory;

    public RedisConfiguration(LettuceConnectionFactory lettuceConnectionFactory) {
        this.lettuceConnectionFactory = lettuceConnectionFactory;
    }

    @Bean("redisTemplate")
    @ConditionalOnProperty(name = "spring.redis.host", matchIfMissing = true)
    public RedisTemplate<String, Object> getSingleRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        RedisSerializer redisObjectSerializer = new RedisObjectSerializer();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(redisObjectSerializer);
        redisTemplate.setHashValueSerializer(redisObjectSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public HashOperations<String, String, String> hashOperations(StringRedisTemplate stringRedisTemplate) {
        return stringRedisTemplate.opsForHash();
    }

    @Bean
    public ListOperations<String,Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    @Bean
    public ZSetOperations<String,Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

    @Bean
    public SetOperations<String,Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    @Bean
    public ValueOperations<String,Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    /**
     * redis工具类
     */
    @Bean("redisUtil")
    public RedisUtil redisUtil(RedisTemplate<String, Object> redisTemplate,
                               StringRedisTemplate stringRedisTemplate,
                               HashOperations<String, String, String> hashOperations,
                               ListOperations<String,Object> listOperations,
                               ZSetOperations<String,Object>zSetOperations,
                               SetOperations<String,Object> setOperations,
                               ValueOperations<String,Object> valueOperations) {
        return new RedisUtil(redisTemplate, stringRedisTemplate,hashOperations,listOperations,
                zSetOperations,setOperations,valueOperations);
    }
}
