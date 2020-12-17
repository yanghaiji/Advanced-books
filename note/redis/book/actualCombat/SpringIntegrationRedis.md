## SpringBoot 集成 Redis

> SpringBoot 2.3.1 

### 添加依赖
这里我们采用的是 `lettuce` 所以必须引入`commons-pool2`

```java
 <dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-data-redis</artifactId>
 </dependency>
 <dependency>
     <groupId>org.apache.commons</groupId>
     <artifactId>commons-pool2</artifactId>
 </dependency>
```

### 配置文件
```java
spring: 
  redis:
    host: 127.0.0.1
    port: 6379
    database: 0
#    password:
    timeout: 6000
# 链接池
    lettuce:
      pool:
        max-active: 10
        max-idle: 10
        max-wait: 0
```

### 自定义 RedisConfiguration
```java
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

```

### 自定义 RedisUtil

这里只截取了一部分，更多的源码请移步source-code 内的redis 目录
```java
public class RedisUtil<K,V> {

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
}
```

### 测试

```java
@RestController
@RequestMapping(value = "/redis/")
public class RedisApiWeb {
    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("test")
    public void redisTest(){
        redisUtil.addStr("redis","Hello World");
        System.out.println(redisUtil.getStr("redis"));
        Map<String,Object> map = new HashMap<>();
        map.put("hash01","hash02");
        redisUtil.setHashTime("myHash",map,100);
        System.out.println(redisUtil.getHashAll("myHash"));
    }

}
```