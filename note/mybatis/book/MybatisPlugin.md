## Mybatis 插件原理与实战

想知道Mybatis的插件是如何生效的就需要了解mybatis的配置，所有的信息都在Mybatis Configuration内部，

在之前的文章中，我们也会看到或如下的代码：

```
public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
    ParameterHandler parameterHandler = mappedStatement.getLang().createParameterHandler(mappedStatement, parameterObject, boundSql);
    // 加载插件
    parameterHandler = (ParameterHandler) interceptorChain.pluginAll(parameterHandler);
    return parameterHandler;
  }
```

### Mybatis 配置文件概览

MyBatis 的配置文件包含了会深深影响 MyBatis 行为的设置和属性信息。 配置文档的顶层结构如下：

- configuration（配置）
  - properties（属性）
  - settings（设置）
  - typeAliases（类型别名）
  - typeHandlers（类型处理器）
  - objectFactory（对象工厂）
  - plugins（插件）
  - environments（环境配置）
    - environment（环境变量）
      - transactionManager（事务管理器）
      - dataSource（数据源）
  - databaseIdProvider（数据库厂商标识）
  - mappers（映射器）

### InterceptorChain

看了Mybatis的源码我们发现，加载插件其实是通过`InterceptorChain`进行加载的；

```java
public class InterceptorChain {

  private final List<Interceptor> interceptors = new ArrayList<>();

  public Object pluginAll(Object target) {
    for (Interceptor interceptor : interceptors) {
      target = interceptor.plugin(target);
    }
    return target;
  }

  public void addInterceptor(Interceptor interceptor) {
    interceptors.add(interceptor);
  }

  public List<Interceptor> getInterceptors() {
    return Collections.unmodifiableList(interceptors);
  }

}
```

在`InterceptorChain` 内部维护了一个集合`Interceptor`（Interceptor 是一个接口）， 然后通过 `pluginAll` 进行循环加载；

---

### 插件（plugins）

MyBatis 允许你在映射语句执行过程中的某一点进行拦截调用。默认情况下，MyBatis 允许使用插件来拦截的方法调用包括：

- Executor (update, query, flushStatements, commit, rollback, getTransaction, close, isClosed)
- ParameterHandler (getParameterObject, setParameters)
- ResultSetHandler (handleResultSets, handleOutputParameters)
- StatementHandler (prepare, parameterize, batch, update, query)

这些类中方法的细节可以通过查看每个方法的签名来发现，或者直接查看 MyBatis 发行包中的源代码。 如果你想做的不仅仅是监控方法的调用，那么你最好相当了解要重写的方法的行为。 因为在试图修改或重写已有方法的行为时，很可能会破坏 MyBatis 的核心模块。 这些都是更底层的类和方法，所以使用插件的时候要特别当心。

通过 MyBatis 提供的强大机制，使用插件是非常简单的，只需实现 Interceptor 接口，并指定想要拦截的方法签名即可。

**如果您不确定拦截了哪些对象，可以在 `Configuration` 搜素 `pluginAll` ;**

---

### 自定义Mybatis 插件

在前面我们说过，插件其本质就是拦截器的原理 ，那么我们实现`Interceptor ` 接口既可以；

```java
@Intercepts({
        @Signature(type = org.apache.ibatis.executor.Executor.class, method = "update",
                args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class ExamplePlugin implements Interceptor {
    private Properties properties = new Properties();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        System.out.println("自定义拦截器已经被执行.....");
        Object returnObject = invocation.proceed();
        // 必要时进行后期处理
        return returnObject;
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}

// 可在在其中获取哪些值
/**
         * 获取被拦截的目前类，在这里是拦截了statementHandler，所有目前类也就是它
         * 通过这个类我们可以拿到待执行的sql语句，通常使用mataObject工具类来获取
         * 关于这个工具类，大家可自行了解，个人认为这个工具类很强大
          */
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        /**
         * 先解释下为什么写成delegate.boundSql就可以拿到boundSql类
         * 从前面也可以得知，statementHandler的默认实现是routingStatementHandler。
         * 这个类有一个属性statementHandler，属性名就叫delegate，而这个属性的默认实现又是preparedStatementHandler
         * 后面这个类又有属性boundSql，所以，最终形成的写法就是delegate.boundSql。
         * 所以这也体现了MetaObject工具类的强大，可以通过实例传参，就可以根据属性名获取对应属性值
         */
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");

        // 待执行的sql，在这里也就是预编译后的sql，即参数位都是?号
        String sql = boundSql.getSql();
        /**
         * 既然拿到了预编译后的sql，那就可以按照你自己的想法为所欲为，如分页，按年分表等等
         * 分表的话，个人推荐druid的sql解析器，我认为还是不错的，大家可以自行了解
         * 最后改造完sql，别忘了把它设置回去
         * metaObject.setValue("delegate.boundSql.sql",sql);
         *  invocation.proceed，即原始方法的执行
         *  注意点就是，因为mybatis插件采用的是代理模式，所以如果存在多个插件，会形成多个代理
         *  你如果要拿到最原始的对象，还得进一步进行分解
         *  如：while(metaObject.getValue(""h) != null){
         *      Object obj = metaObject.getValue("h");
         *       ....
         *  }
         */
        return invocation.proceed();
```

**配置插件**

- XML配置

  ```xml
  <!-- mybatis-config.xml -->
  <plugins>
    <plugin interceptor="com.javayh.mybatis.config.ExamplePlugin">
      <property name="someProperty" value="100"/>
    </plugin>
  </plugins>
  ```

- java配置类

  ```java
  @org.springframework.context.annotation.Configuration
  public class MapperConfig {
  
      //注册插件
      @Bean
      public ExamplePlugin myPlugin() {
          ExamplePlugin myPlugin = new ExamplePlugin();
          //设置参数，比如阈值等，可以在配置文件中配置，这里直接写死便于测试
          Properties properties = new Properties();
          //这里设置慢查询阈值为1毫秒，便于测试
          properties.setProperty("time", "1");
          myPlugin.setProperties(properties);
          return myPlugin;
      }
      
      //将插件加入到mybatis插件拦截链中
      /*@Bean
      public ConfigurationCustomizer configurationCustomizer() {
          return configuration -> {
              //插件拦截链采用了责任链模式，执行顺序和加入连接链的顺序有关
              ExamplePlugin myPlugin = new ExamplePlugin();
              //设置参数，比如阈值等，可以在配置文件中配置，这里直接写死便于测试
              Properties properties = new Properties();
              //这里设置慢查询阈值为1毫秒，便于测试
              properties.setProperty("time", "1");
              myPlugin.setProperties(properties);
              configuration.addInterceptor(myPlugin);
          };
      }*/
  }
  ```

上面的插件将会拦截在 Executor 实例中所有的 “update 、query” 方法调用， 这里的 Executor 是负责执行底层映射语句的内部对象。

**提示** **覆盖配置类**

除了用插件来修改 MyBatis 核心行为以外，还可以通过完全覆盖配置类来达到目的。只需继承配置类后覆盖其中的某个方法，再把它传递到 SqlSessionFactoryBuilder.build(myConfig) 方法即可。再次重申，这可能会极大影响 MyBatis 的行为，务请慎之又慎。