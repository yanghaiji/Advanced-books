## Redis开发规范

[1] 强制 [2] 推荐 [3] 参考

## 使用规范

1. [2] 冷热数据区分

   > 虽然 Redis支持持久化，但将所有数据存储在 Redis 中，成本非常昂贵。建议将热数据 (如 QPS超过 5k) 的数据加载到 Redis 中。低频数据可存储在 Mysql、 ElasticSearch中。

2. [2] 业务数据分离

   > 不要将不相关的数据业务都放到一个 Redis中。一方面避免业务相互影响，另一方面避免单实例膨胀，并能在故障时降低影响面，快速恢复。

3. [2] 缓存不能有中间态

   > 缓存应该仅作缓存用，去掉后业务逻辑不应发生改变，万不可切入到业务里。第一，缓存的高可用会影响业务；第二，产生深耦合会发生无法预料的效果；第三，会对维护行产生肤效果。

## Key设计规范

1. [2] 可读性和可管理性：以英文冒号分隔key，前缀概念的范围的返回从大到小，从不变到可变，从变化幅度小到变化幅度大。

   > 例如：`yoga:user:1`，表示 yoga:user:{userID}，即瑜伽子系统ID=1的用户信息。

2. [2] 简洁性：保证语义的前提下，控制key的长度，当key较长时，内存占用也不容忽视。

   > 例如：`user:{uid}:friends:messages:{mid}`可简化为 `u:{uid}:f:m:{mid}`。

3. [1] 不包含特殊字符，只使用字母数字。

> 反例：包含空格、换行、单双引号以及其他转义字符

## Value设计规范

1. [1] 拒绝bigkey(防止网卡流量、慢查询)

   > string类型控制在`10K`以内，hash、list、set、zset元素个数不要超过5000。

   > 反例：一个包含200万个元素的list。

   > 非字符串的bigkey，不要使用del删除，使用hscan、sscan、zscan方式渐进式删除，同时要注意防止bigkey过期时间自动删除问题(例如一个200万的zset设置1小时过期，会触发del操作，造成阻塞，而且该操作不会不出现在慢查询中(latency可查)).

2. [2] 选择适合的数据类型。

   > 例如：实体类型(要合理控制和使用数据结构内存编码优化配置,例如ziplist，但也要注意节省内存和性能之间的平衡)

   > 反例：
   > set user:1:name tom
   > set user:1:age 19
   > set user:1:favor football

   > 正例:
   > hmset user:1 name tom age 19 favor football

3. [2] 控制key的生命周期，redis不是垃圾桶。

   > 建议使用expire设置过期时间(条件允许可以打散过期时间，防止集中过期)，不过期的数据重点关注idletime。
   > 作为缓存使用的 Key，必须要设置失效时间。失效时间并不是越长越好，请根据业务性质进行设置。注意，失效时间的单位有的是秒，有的是毫秒，这个很多同学不注意容易搞错。

## 命令使用

1. [2] O(N)命令关注N的数量

   > 例如hgetall、lrange、smembers、zrange、sinter等并非不能使用，但是需要明确N的值。
   > 有遍历的需求可以使用hscan、sscan、zscan代替。

2. [2] 禁用命令

   > 禁止线上使用keys、flushall、flushdb等，通过redis的rename机制禁掉命令，或者使用scan的方式渐进式处理。

3. [2] 严禁不设置范围的批量操作

   > redis 那么快，慢查询除了网络延迟，就属于这些批量操作函数。大多数线上问题都是由于这些函数引起。
   >
   > - [zset] 严禁对 zset 的不设范围操作
   > - ZRANGE、 ZRANGEBYSCORE等多个操作 ZSET 的函数，严禁使用 ZRANGE myzset 0 -1 等这种不设置范围的操作。请指定范围，如 ZRANGE myzset 0 100。如不确定长度，可使用 ZCARD 判断长度
   > - [hash] 严禁对大数据量 Key 使用 HGETALL
   > - HGETALL会取出相关 HASH 的所有数据，如果数据条数过大，同样会引起阻塞，请确保业务可控。如不确定长度，可使用 HLEN 先判断长度
   > - [key] Redis Cluster 集群的 mget 操作，会到各分片取数据聚合，相比传统的 M/S架构，性能会下降很多，请提前压测和评估
   > - [其他] 严禁使用 sunion, sinter, sdiff等一些聚合操作

4. [2] 合理使用select

   > redis的多数据库较弱，使用数字进行区分，很多客户端支持较差，同时多业务用多数据库实际还是单线程处理，会有干扰。
   > select函数用来切换database，对于使用方来说，这是很容易发生问题的地方，cluster模式也不支持多个 database，且没有任何收益，慎用。

5. [2] 使用批量操作提高效率

   > 原生命令：例如mget、mset。
   > 非原生命令：可以使用pipeline提高效率。
   > 但要注意控制一次批量操作的元素个数(例如500以内，实际也和元素字节数有关)。

   注意两者不同：

   - 原生是原子操作，pipeline是非原子操作。
   - pipeline可以打包不同的命令，原生做不到。
   - pipeline需要客户端和服务端同时支持。

6. [2] Redis事务功能较弱，不建议过多使用

   > Redis的事务功能较弱(不支持回滚)，而且集群版本(自研和官方)要求一次事务操作的key必须在一个slot上(可以使用hashtag功能解决)

7. [2] Redis集群版本在使用Lua上有特殊要求：

   > 所有key都应该由 KEYS 数组来传递，redis.call/pcall 里面调用的redis命令，key的位置，必须是KEYS array, 否则直接返回error，"-ERR bad lua script for redis cluster, all the keys that the script uses should be passed using the KEYS array"
   > 所有key，必须在1个slot上，否则直接返回error, "-ERR eval/evalsha command keys must in same slot"

8. [2] 必要情况下使用monitor命令时，要注意不要长时间使用。

## 客户端使用

1. [2] 避免多个应用使用一个Redis实例

   > 正例：不相干的业务拆分，公共数据做服务化。

2. [2] 使用带有连接池的数据库，可以有效控制连接，同时提高效率，标准使用方式：

   ```
   Jedis jedis = null;
   try {
       jedis = jedisPool.getResource();
       // 具体的命令
       jedis.executeCommand()
   } catch (Exception e) {
       logger.error("op key {} error: " + e.getMessage(), key, e);
   	throw e;
   } finally {
       // 注意这里不是关闭连接，在JedisPool模式下，Jedis会被归还给资源池。
       if (jedis != null) 
           jedis.close();
   }
   ```

3. [2] 高并发下建议客户端添加熔断功能 (例如 netflix hystrix)

4. [2] 设置合理的密码，如有必要可以使用 SSL 加密访问

5. [2] 根据自身业务类型，选好 maxmemory-policy(最大内存淘汰策略)，设置好过期时间。

   > 默认策略是volatile-lru，即超过最大内存后，在过期键中使用 lru算法进行key的剔除，保证不过期数据不被删除，但是可能会出现 OOM 问题。
   > 其他策略如下
   >
   > - allkeys-lru：根据 LRU 算法删除键，不管数据有没有设置超时属性，直到腾出足够空间为止。
   > - allkeys-random：随机删除所有键，直到腾出足够空间为止。
   > - volatile-random: 随机删除过期键，直到腾出足够空间为止。
   > - volatile-ttl：根据键值对象的 ttl 属性，删除最近将要过期数据。如果没有，回退到 noeviction 策略。
   > - noeviction：不会剔除任何数据，拒绝所有写入操作并返回客户端错误信息 "(error) OOM command not allowed when used memory"，此时 Redis 只响应读操作。

## 相关工具

1. [2] 数据同步 redis 间数据同步可以使用：redis-port

2. [2] big key搜索[redis大key搜索工具](https://yq.aliyun.com/articles/117042)

3. [2] 热点 key 寻找 (内部实现使用 monitor，所以建议短时间使用), [facebook的redis-faina](https://github.com/facebookarchive/redis-faina)

4. [2] 删除 bigkey

   > redis 4.0 已经支持 key 的异步删除