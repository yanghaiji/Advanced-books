## 接口幂等性与实战

**一、幂等性概念**
在编程中.一个幂等操作的特点是其任意多次执行所产生的影响均与一次执行的影响相同。
幂等函数，或幂等方法，是指可以使用相同参数重复执行，并能获得相同结果的函数。
这些函数不会影响系统状态，也不用担心重复执行会对系统造成改变。
例如，“getUsername()和setTrue()”函数就是一个幂等函数. 更复杂的操作幂等保证是利用唯一交易号(流水号)实现.

我的理解：幂等就是一个操作，不论执行多少次，产生的效果和返回的结果都是一样的。



**二、幂等性场景**
1、查询操作：查询一次和查询多次，在数据不变的情况下，查询结果是一样的。select是天然的幂等操作；

2、删除操作：删除操作也是幂等的，删除一次和多次删除都是把数据删除。(注意可能返回结果不一样，删除的数据不存在，返回0，删除的数据多条，返回结果多个) ；

3、唯一索引：防止新增脏数据。比如：支付宝的资金账户，支付宝也有用户账户，每个用户只能有一个资金账户，怎么防止给用户创建资金账户多个，那么给资金账户表中的用户ID加唯一索引，所以一个用户新增成功一个资金账户记录。要点：唯一索引或唯一组合索引来防止新增数据存在脏数据（当表存在唯一索引，并发时新增报错时，再查询一次就可以了，数据应该已经存在了，返回结果即可）；

4、token机制：防止页面重复提交。

原理上通过session token来实现的(**也可以通过redis来实现**)。当客户端请求页面时，服务器会生成一个随机数Token，并且将Token放置到session当中，然后将Token发给客户端（一般通过构造hidden表单）。
下次客户端提交请求时，Token会随着表单一起提交到服务器端。

服务器端第一次验证相同过后，会将session中的Token值更新下，若用户重复提交，第二次的验证判断将失败，因为用户提交的表单中的Token没变，但服务器端session中Token已经改变了。

5、悲观锁
获取数据的时候加锁获取。select * from table_xxx where id='xxx' for update; 注意：id字段一定是主键或者唯一索引，不然是锁表，会死人的；悲观锁使用时一般伴随事务一起使用，数据锁定时间可能会很长，根据实际情况选用；

6、乐观锁——乐观锁只是在更新数据那一刻锁表，其他时间不锁表，所以相对于悲观锁，效率更高。乐观锁的实现方式多种多样可以通过version或者其他状态条件：
\1. 通过版本号实现update table_xxx set name=#name#,version=version+1 where version=#version#如下图(来自网上)；
\2. 通过条件限制 update table_xxx set avai_amount=avai_amount-#subAmount# where avai_amount-#subAmount# >= 0要求：quality-#subQuality# >= ，这个情景适合不用版本号，只更新是做数据安全校验，适合库存模型，扣份额和回滚份额，性能更高；

7、分布式锁

如果是分布是系统，构建全局唯一索引比较困难，例如唯一性的字段没法确定，这时候可以引入分布式锁，通过第三方的系统(redis或zookeeper)，在业务系统插入数据或者更新数据，获取分布式锁，然后做操作，之后释放锁，这样其实是把多线程并发的锁的思路，引入多多个系统，也就是分布式系统中得解决思路。要点：某个长流程处理过程要求不能并发执行，可以在流程执行之前根据某个标志(用户ID+后缀等)获取分布式锁，其他流程执行时获取锁就会失败，也就是同一时间该流程只能有一个能执行成功，执行完成后，释放分布式锁(分布式锁要第三方系统提供)；

8、select + insert
并发不高的后台系统，或者一些任务JOB，为了支持幂等，支持重复执行，简单的处理方法是，先查询下一些关键数据，判断是否已经执行过，在进行业务处理，就可以了。注意：核心高并发流程不要用这种方法；

9、状态机幂等
在设计单据相关的业务，或者是任务相关的业务，肯定会涉及到状态机(状态变更图)，就是业务单据上面有个状态，状态在不同的情况下会发生变更，一般情况下存在有限状态机，这时候，如果状态机已经处于下一个状态，这时候来了一个上一个状态的变更，理论上是不能够变更的，这样的话，保证了有限状态机的幂等。注意：订单等单据类业务，存在很长的状态流转，一定要深刻理解状态机，对业务系统设计能力提高有很大帮助

10、对外提供接口的api如何保证幂等
如银联提供的付款接口：需要接入商户提交付款请求时附带：source来源，seq序列号；source+seq在数据库里面做唯一索引，防止多次付款(并发时，只能处理一个请求) 。
重点：对外提供接口为了支持幂等调用，接口有两个字段必须传，一个是来源source，一个是来源方序列号seq，这个两个字段在提供方系统里面做联合唯一索引，这样当第三方调用时，先在本方系统里面查询一下，是否已经处理过，返回相应处理结果；没有处理过，进行相应处理，返回结果。注意，为了幂等友好，一定要先查询一下，是否处理过该笔业务，不查询直接插入业务系统，会报错，但实际已经处理了。

### Token 机制实战

针对前端重复连续多次点击的情况，例如恶意评论，支付，提交订单的接口就可以通过 Token 的机制实现防止重复提交。
- 流程图如下:

![token_limiter](img/token_limiter.png)

整个流程已经完成，但是我们在实际开发的时候还需要制定规则，来判断是否是重复提交

例如： 同一商家用户支付金额的频率，如果过在几秒内，
像同一商家支付相同的金额，我们是不是要做一些判断，当然如果上次支付成功和失败对我的判断有着重要的决定
可以看出指定相关的规则对我们也有着至关重要的作用

- 代码实战
由于是演示的代码，这里并没有采用redis，而是使用了Map作为本地缓存
大致的流程如下：

1. 当用户访问核心业务，先生成一个token
2. 当提交业务时，我们需要校验token的合法性，这里采用的时根据失效时间
3. 自定义限流注解，以便于根据不同的场景设置不同的时间
4. 使用Spring的拦截器，进行前置拦截

**示例代码在source-code内**

**本地缓存Map**
```java
public class LocalCacheMap {

    private final static ConcurrentHashMap<String,Long> LOCAL_CACHE_MAP
                                            = new ConcurrentHashMap<>(256);

    /**
     * 锁定
     * @param key   key
     * @param value 值
     */
    public static void setCache(String key,Long value){
        LOCAL_CACHE_MAP.put(key,value);
    }

    /**
     * 检验是否讯在
     * @param key
     * @return
     */
    public static Long getCache(String key){
        return LOCAL_CACHE_MAP.get(key);
    }

    /**
     * 删除cache
     * @param key
     * @return
     */
    public static boolean remove(String key){
        try {
            LOCAL_CACHE_MAP.remove(key);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
       return true;
    }

}
```

**Token接口**
```java
public class LimiterTokenServiceImpl implements LimiterTokenService{

    /**
     * 生成token
     */
    @Override
    public String createLimiterToken() {
        // 生成token
        String token = UUID.randomUUID().toString();
        addTokenTime(token);
        return token;
    }

    /**
     * 校验当前token是否被锁定 true: 锁定  | false: 未锁定
     * @param token
     * @return
     */
    @Override
    public boolean checkToken(String token ,Long time) {
        Long l = System.currentTimeMillis();
        Long cache = LocalCacheMap.getCache(token);
        if(Objects.nonNull(cache)){
            if (l - cache > time) {
                return true;
            }
            // 删除
            LocalCacheMap.remove(token);
            return false;
        }
        LocalCacheMap.remove(token);
        return false;
    }

    /**
     * 添加时间
     * 这一步是为模拟过期时间
     * @param token key
     */
    @Override
    public void addTokenTime(String token) {
        // 放入第一次的请求时间
        long millis = System.currentTimeMillis();
        LocalCacheMap.setCache(token,millis);
    }
}
```
**自定义注解**
```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoIdempotent {

    String value() default "";

    long time() default 1000L;

}
```

**自定义前置处理**

```java
@Configuration
public class AutoIdempotentInterceptor implements HandlerInterceptor {
    @Resource
    private LimiterTokenService limiterTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        //被ApiIdempotment标记的扫描
        AutoIdempotent methodAnnotation = method.getAnnotation(AutoIdempotent.class);
        if(methodAnnotation != null) {
            try{
                long time = methodAnnotation.time();
                String token = request.getParameter("token");
                // 幂等性校验, 校验通过则放行, 校验失败则抛出异常, 并通过统一异常处理返回友好提示
                return limiterTokenService.checkToken(token,time);

            }catch(Exception ex){
                throw ex;
            }
        }
        //必须返回true,否则会被拦截一切请求
        return true;
    }
}
```
**注册拦截器**
```java
@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Resource
    private AutoIdempotentInterceptor autoIdempotentInterceptor;

    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autoIdempotentInterceptor)
                .addPathPatterns("/**/*");
    }
}
```

### 在有些时候我们也可以根据 IP 进行访问次数的现实

其核心思想合Token机制差不多，这里就直接上代码了，大家一看就会明白的

**自定义注解**

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SysLog {

    String value() default "";

    long limit() default 10;
}
```
**AOP 实现**
```java
public class SysLogAspect {

    private Map<String,AtomicLong> limiter = new ConcurrentHashMap<>(256);

    private final HttpServletRequest request;

    public SysLogAspect(HttpServletRequest request) {
        this.request = request;
    }

    @Around("@annotation(sysLog)")
    public Object getLog(ProceedingJoinPoint joinPoint, SysLog sysLog) throws Throwable {
        Object proceed = null;
        long time = System.currentTimeMillis();
        try {
            proceed = joinPoint.proceed();
            time = System.currentTimeMillis() - time;
            return proceed;
        }
        catch (Throwable throwable) {
            throw throwable;
        }
        finally {
            // 方法执行后
            postOperation(joinPoint,time);

        }
    }

    /**
     * <p>
     *       方法执行完之后的操作
     * </p>
     * @version 1.0.0
     * @author hai ji
     * @since 2020/8/11
     * @param joinPoint
     * @param time
     * @return void
     */
    private void postOperation(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //类名
        String className = methodSignature.getDeclaringTypeName() + "." + methodSignature.getName();
        //将类名加 ip 作为 key
        String limitKey = className + getIpAddr();
        SysLog annotation = methodSignature.getMethod().getAnnotation(SysLog.class);
        String value = annotation.value();
        long limit = annotation.limit();
        inputCheckLimit(limit);
        //首先去缓存根据key 取value
        if(limiter.get(limitKey) != null){
            AtomicLong atomicLong1 = limiter.get(limitKey);
            long li = atomicLong1.get();
            if(li > 0){
                log.info("当前访问访问次数 : {}", atomicLong1);
                atomicLong1.decrementAndGet();
                limiter.put(limitKey, atomicLong1);
                log.info("剩余访问访问次数 : {}", atomicLong1);
            }else {
                limiter(li,limitKey);
            }
        }else {
            AtomicLong atomicLong = new AtomicLong(limit);
            log.info("当前访问访问次数 : {}",atomicLong.get());
            //减少范文次数
            atomicLong.decrementAndGet();
            limiter.put(limitKey,atomicLong);
            log.info("剩余访问访问次数 : {}",atomicLong.get());
        }
        log.info(value+"执行耗时: {} s",time);
    }

    protected void inputCheckLimit(long limit) {
        if(limit < 0){
            throw new RuntimeException("Current limiting times must be greater than 0");
        }
    }

    protected void limiter(Long lo,String limitKey){
        if(lo <= 0){
            //根据key删除
            limiter.remove(limitKey);
            log.error(getIpAddr() +"  The visit is too frequent. Please try again later");
            throw new RuntimeException("The visit is too frequent. Please try again later");
        }
    }

    private String getIpAddr() {
        String ip = request.getHeader("x-forwarded-for");
        if(ip ==null || ip.length() ==0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip ==null || ip.length() ==0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip ==null || ip.length() ==0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if("0:0:0:0:0:0:0:1".equalsIgnoreCase(ip)){
            ip = "127.0.0.1";
        }
        return ip;
    }

}
```

**个人理解，不足之处还请多多包含**
