## @Transaction 注解全面解析

-[注解属性介绍](#注解属性介绍)
-[使用方式的总结](#使用方式的总结)

### 注解属性介绍

首先开看看源码

```bash
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Transactional {
    @AliasFor("transactionManager")
    String value() default "";

    @AliasFor("value")
    String transactionManager() default "";

    Propagation propagation() default Propagation.REQUIRED;

    Isolation isolation() default Isolation.DEFAULT;

    int timeout() default -1;

    boolean readOnly() default false;

    Class<? extends Throwable>[] rollbackFor() default {};

    String[] rollbackForClassName() default {};

    Class<? extends Throwable>[] noRollbackFor() default {};

    String[] noRollbackForClassName() default {};
}

12345678910111213141516171819202122232425262728
```

- **value 和 transactionManager 属性**
  它们两个是一样的意思。当配置了多个事务管理器时，可以使用该属性指定选择哪个事务管理器。
  在默认的代理模式下，只有目标方法由外部调用，才能被 Spring 的事务拦截器拦截。在同一个类中的两个方法直接调用，是不会被 Spring 的事务拦截器拦截

- **propagation 属性**
  事务的传播行为，默认值为 Propagation.REQUIRED
  可选的值有：

  **Propagation.REQUIRED**
  如果当前存在事务，则加入该事务，如果当前不存在事务，则创建一个新的事务。

  **Propagation.SUPPORTS**
  如果当前存在事务，则加入该事务；如果当前不存在事务，则以非事务的方式继续运行。

  **Propagation.MANDATORY**
  如果当前存在事务，则加入该事务；如果当前不存在事务，则抛出异常。

  **Propagation.REQUIRES_NEW**
  重新创建一个新的事务，如果当前存在事务，暂停当前的事务。

  **Propagation.NOT_SUPPORTED**
  以非事务的方式运行，如果当前存在事务，暂停当前的事务。

  **Propagation.NEVER**
  以非事务的方式运行，如果当前存在事务，则抛出异常。

  **Propagation.NESTED**
  和 Propagation.REQUIRED 效果一样。

- isolation 属性
  事务的隔离级别，默认值为 Isolation.DEFAULT。

  可选的值有：
  **Isolation.DEFAULT**
  使用底层数据库默认的隔离级别。
  **Isolation.READ_UNCOMMITTED**
  读未提交
  **Isolation.READ_COMMITTED**
  提交读
  **Isolation.REPEATABLE_READ**
  可重复读
  **Isolation.SERIALIZABLE**
  可序列化

- timeout 属性
  事务的超时时间，默认值为-1。如果超过该时间限制但事务还没有完成，则自动回滚事务。

- readOnly 属性
  指定事务是否为只读事务，默认值为 false；为了忽略那些不需要事务的方法，比如读取数据，可以设置 read-only 为 true。

- rollbackFor 属性
  用于指定能够触发事务回滚的异常类型，可以指定多个异常类型。

- noRollbackFor 属性
  抛出指定的异常类型，不回滚事务，也可以指定多个异常类型。
  
### 使用方式的总结

 1. @Transactional注解 不能作用在private 方法上
 2. 带有事务的方法，调用本类中不带有事务的方法，会将本类不带有事务的方法纳入到事务内，
    这是调用的方法，可以是私有方法,即走的是最外层方法的事务
 3. A类 无事务的方法，调用 B类有事务的方法时， B类异常可以进行事务回滚，但是A类无法回滚
 4. A类有事务的方法，调用B类无事务的方法，当B类发生异常，可以正常进行事务回滚
 5. 设置事务回滚的两种方式
    5.1 进行 throw new Exception
    5.2 可以在需要设置手动回滚TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    
这里只是个人的总计，有不足之处，还请大家多多指出！
    
    