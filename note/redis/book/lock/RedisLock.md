## Redis实现分布式锁 

分布式锁是控制分布式系统之间同步访问共享资源的一种方式。

在分布式系统中，常常需要协调他们的动作。如果不同的系统或是同一个系统的不同主机之间共享了一个或一组资源，那么访问这些资源的时候，往往需要互斥来防止彼此干扰来保证一致性，这个时候，便需要使用到分布式锁

### 无锁的应用

```java
    @GetMapping(value = "test")
    public void test() {
        ReentrantLock reentrantLock = new ReentrantLock();
        reentrantLock.lock();
        try {
            order();
        }finally {
            reentrantLock.unlock();
        }
    }
```

我们在开发应用的时候，如果需要对某一个共享变量进行多线程同步访问的时候，可以使用我们学到的锁进行处理，并且可以完美的运行，毫无Bug！

但是如果是分布式环境下呢？这时就必须采用分布式锁，才能保证所有服务操作资源的一致性，分布式锁有很多种实现方式，如：数据库、Redis、zookeeper....今天我们将介绍Redis实现分布式锁的演进流程，和其中的一些坑。

---

### Redis实现分布式锁的演进流程

上面的代码在分布式情况下肯定是有问题的，那我稍加调整一下，引入Redis

#### 第一次演进

```java
    @GetMapping(value = "test2")
    public void test2() {
        Boolean javayh = redisTemplate.opsForValue().setIfAbsent(RedisKey.key("redis-order-lock"), "javayh");
        try {
            if (javayh) {
                order();
            }//实现自旋
            else {
                test2();
            }
        } finally {
            redisTemplate.delete(RedisKey.key("redis-order-lock"));
        }
    }
```

**大家看这段代码有什么问题？**

> 在所有的一切都是按照我们的预期去执行的，好像没什么问题，但是并发下往往不会按照我的预期去执行。

**问题**

> 在分布式中，其中一个线程得到了锁，进行执行，其他的线程进行不断的尝试获取锁，但是如果获取到锁的服务器挂了，没有释放锁，这就会造成死锁...

#### 第二次演进

上面的问题似乎出现了加锁后，没有执行释放锁的代码，那么我们是不是可以给锁设置过期时间，实现到期自动删除

```java
    @GetMapping(value = "test3")
    public void test3() {
        String key = RedisKey.key("redis-order-lock");
        // 上面的代码 没有办法释放锁，那好，我们给他指定失效时间，但是这里有没有坑呢
        Boolean javayh = redisTemplate.opsForValue().setIfAbsent(key, "javayh");
        try {
            redisTemplate.expire(key, 30, TimeUnit.SECONDS);
            if (javayh) {
                order();
            }//实现自旋
            else {
                test2();
            }
        } finally {
            redisTemplate.delete(key);
        }
    }
```

**大家看这段代码有什么问题？**

**问题**

> 但是就像之前一下，在没有执行给锁设置过期时间，服务器就挂了呢？是不是也会造成死锁。也就是锁，上下两个操作不是原子的操作。

#### 第三次演进

知道了问题所在我们继续改。

```java
    @GetMapping(value = "test4")
    public void test4() {
        // 上面的代码 没有办法释放锁，那我们将加锁和设置失效时间的代码放在一起就可以
        // 这样好像看似没什么问题了，但是确实是这样吗？
        String key = RedisKey.key("redis-order-lock");
        Boolean javayh = redisTemplate.opsForValue().setIfAbsent(key, "javayh", 30, TimeUnit.SECONDS);
        try {
            if (javayh) {
                order();
            }//实现自旋
            else {
                test4();
            }
        } finally {
           redisTemplate.delete(key);
           
        }
    }
```

**大家看这段代码有什么问题？**

**问题**

>  我们在来分析一下：
>  加入 这是有三个线程，其中一个线程获取了锁，执行了起来，但是性能很慢，超过了我们的过期时间， 这时锁已经被释放，其他线程就可以进行获取锁，但是当第一个线程执行完业务逻辑，想要删除这把锁、，这时就会吧其他线程锁住的资源进行释放了，这也是坑根据上面的分析，我们可以将key重新设置一下，修改后的代码

#### 第四次演进

```java
    @GetMapping(value = "test4")
    public void test4() {
      	// 重新生成的key
        String key = RedisKey.key("redis-order-lock") + UUID.randomUUID().toString();
        Boolean javayh = redisTemplate.opsForValue().setIfAbsent(key, "javayh", 30, TimeUnit.SECONDS);
        try {
            if (javayh) {
                order();
            }//实现自旋
            else {
                test4();
            }
        } finally {
            Object o = redisTemplate.opsForValue().get(key);
            if (o.equals(key)) {
                redisTemplate.delete(key);
            }
        }
    }
```

**大家看这段代码有什么问题？**

**问题**

> 这样看起来好像没什么问题，还加了判断锁与redis的锁是不是一致的，但是Redis的官方并不推荐我们这样操作，他更希望我们可以使用脚本在进行。

#### 第五次演进

```java
    @GetMapping(value = "test5")
    public void test5() {
        // 这里看似ok。我们先不看
        String key = RedisKey.key("redis-order-lock") + UUID.randomUUID().toString();
        Boolean javayh = redisTemplate.opsForValue().setIfAbsent(key, "javayh", 30, TimeUnit.SECONDS);
        try {
            if (javayh) {
                order();
            }//实现自旋
            else {
                test2();
            }
        } finally {
            // 官方建议使用脚本操作
            String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                    "then\n" +
                    "    return redis.call(\"del\",KEYS[1])\n" +
                    "else\n" +
                    "    return 0\n" +
                    "end";
            redisTemplate.execute(new DefaultRedisScript<Long>(script), Arrays.asList(key), key);
        }
    }
```

最终的演进到这里就差不多了，当然这只是demo，问题肯定还是有的。



这一切的演进其实都来源于Redis官方的说明，如下：

> The command `SET resource-name anystring NX EX max-lock-time` is a simple way to implement a locking system with Redis.
>
> A client can acquire the lock if the above command returns `OK` (or retry after some time if the command returns Nil), and remove the lock just using DEL.
>
> The lock will be auto-released after the expire time is reached.
>
> It is possible to make this system more robust modifying the unlock schema as follows:
>
> - Instead of setting a fixed string, set a non-guessable large random string, called token.
> - Instead of releasing the lock with DEL, send a script that only removes the key if the value matches.
>
> This avoids that a client will try to release the lock after the expire time deleting the key created by another client that acquired the lock later.
>
> An example of unlock script would be similar to the following:
>
> ```
> if redis.call("get",KEYS[1]) == ARGV[1]
> then
>     return redis.call("del",KEYS[1])
> else
>     return 0
> end
> ```
>
> The script should be called with `EVAL ...script... 1 resource-name token-value`



