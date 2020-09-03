## Spring FactoryBean

- [FactoryBean简介](#FactoryBean简介)
- [FactoryBean与BeanFactory区别](#FactoryBean与BeanFactory区别)
- [FactoryBean源码](#FactoryBean源码)
- [FactoryBean应用](#FactoryBean应用)
- [FactoryBean在Spring中的应该](#FactoryBean在Spring中的应该)

### FactoryBean简介

在此之前我们说过`BeanFactory`,请千万不要将`FactoryBean`与`BeanFactory` 搞混了，毕竟他们有着天壤之别;

接口将由BeanFactory中使用的对象实现，这些对象本身就是单个对象的工厂。如果一个bean实现了这个接口，
它将被用作一个要公开的对象的工厂，而不是直接作为一个将要公开的bean实例。

注意：实现此接口的bean不能用作普通bean。FactoryBean是在bean样式中定义的，
但是为bean引用公开的对象（getObject（））始终是它创建的对象。

FactoryBean可以支持 `singletons`和` prototypes`,，并且可以根据需要缓慢地创建对象，或者在启动时急切地创建对象。
SmartFactoryBean接口允许公开更细粒度的行为元数据。

该接口在框架本身中大量使用，例如用于AOP ProxyFactoryBean或JndiObjectFactoryBean。它也可以用于自定义组件；
但是，这只在基础结构代码中常见。

FactoryBean是一个程序化的契约。实现不应该依赖注释驱动的注入或其他反射工具。getObjectType（）getObject（）
调用可能在引导过程的早期到达，甚至在任何后处理器设置之前。如果需要访问其他bean，请实现BeanFactoryAware并以编程方式获取它们。

容器只负责管理FactoryBean实例的生命周期，而不负责由FactoryBean创建的对象的生命周期。
因此，对公开的bean对象（如关闭。关闭不会自动调用（）。相反，
FactoryBean应该实现DisposableBean并将任何这样的close调用委托给底层对象。

最后，FactoryBean对象参与包含BeanFactory的bean创建同步。通常不需要内部同步，
除了为了在FactoryBean本身（或类似的）中进行延迟初始化。

### FactoryBean与BeanFactory区别

>BeanFactory是个Factory，也就是IOC容器或对象工厂，FactoryBean是个Bean。
>在Spring中，所有的Bean都是由BeanFactory(也就是IOC容器)来进行管理的。
>但对FactoryBean而言，这个Bean不是简单的Bean，而是一个能生产或者修饰对象生成的工厂Bean,
>它的实现与设计模式中的工厂模式和修饰器模式类似 

### FactoryBean源码
```java
public interface FactoryBean<T> {
    String OBJECT_TYPE_ATTRIBUTE = "factoryBeanObjectType";
    //返回此工厂管理的对象的实例（可能是共享的或独立的）。
    //与一样BeanFactory，这允许同时支持Singleton和Prototype设计模式。
    //如果在调用时尚未完全初始化此FactoryBean（例如，因为它包含在循环引用中），则抛出一个FactoryBeanNotInitializedException。
	T getObject() throws Exception;
    //返回此FactoryBean创建的对象的类型
    //这样一来，无需实例化对象即可检查特定类型的bean，例如在自动装配时。
    //对于创建单例对象的实现，此方法应尽量避免创建单例。它应该提前估计类型。对于原型，建议在此处返回有意义的类型。
    //可以在完全初始化此FactoryBean 之前调用此方法。它一定不能依赖初始化期间创建的状态。当然，如果可用，它仍然可以使用这种状态。
    //注意：自动装配将仅忽略返回null此处的FactoryBean 。因此，强烈建议使用FactoryBean的当前状态正确实现此方法。
	Class<?> getObjectType();
    //该工厂管理的对象是单例吗？也就是说，将getObject()始终返回相同的对象
    //如果FactoryBean指示保留单个对象，则从其返回的对象getObject()可能会被拥有的BeanFactory缓存。
    // 因此，true 除非FactoryBean始终公开相同的引用，否则不要返回。
    //FactoryBean本身的单例状态通常由拥有的BeanFactory提供；通常，它必须在那里定义为单例。
    //注意：此方法返回false不一定表示返回的对象是独立的实例。
    // 扩展SmartFactoryBean接口的实现可以通过其SmartFactoryBean.isPrototype()方法显式指示独立的实例 。
    // FactoryBean 不实现这个扩展接口的实现，简单地认为总是返回如果独立实例 isSingleton()实现返回false。
    //默认实现返回true，因为 FactoryBean通常会管理一个单例实例
	default boolean isSingleton() {
		return true;
	}
}
```

### FactoryBean应用
这里我们用到了`InitializingBean`，您也可以不用的，直接 `new` 返回即可
详细的源代码[FactoryBeanLearn.java](../../../../source-code/src/main/java/com/javayh/advanced/spring/bean/FactoryBeanLearn.java)
```java
@Configuration
public class FactoryBeanLearn implements FactoryBean<BaseBean>,InitializingBean {

    Logger log  = LoggerFactory.getLogger(FactoryBeanLearn.class);

    private BaseBean baseBean;

    private final CustomConfigurationProperties customConfigurationProperties;

    public FactoryBeanLearn(CustomConfigurationProperties customConfigurationProperties) {
        this.customConfigurationProperties = customConfigurationProperties;
        log.info("customConfigurationProperties init : {}",customConfigurationProperties.toString());
    }

    @Override
    public BaseBean getObject() throws Exception {
        if(Objects.isNull(baseBean)){
            this.afterPropertiesSet();
        }
        return this.baseBean;
    }

    @Override
    public Class<?> getObjectType() {
        return BaseBean.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("实例化 FactoryBeanLearn");
        this.baseBean = this.buildBaseBean();
    }

    /**
     * <p>
     *       实例化对象
     * </p>
     * @param
     * @return com.javayh.advanced.spring.bean.BaseBean
     */
    private BaseBean buildBaseBean() {
        if (Objects.isNull(customConfigurationProperties)){
            throw new RuntimeException("customConfigurationProperties is null");
        }
        return BaseBean.builder()
                .beanId(customConfigurationProperties.getGender())
                .beanName(customConfigurationProperties.getAuthor()).build();
    }

}
```

### FactoryBean在Spring中的应该

这里需要您对`SpringIoc`源码有一些了解,若还不了解，请先阅读一下几篇文章，会对接下来的阅读有所帮助
- [SpringApplication 运行及源码解读](../ioc/SpringApplication.md)
- [refresh 源码解析](../ioc/refresh.md)
- [Spring Bean 的创建](../bean/InitBean.md)

接下的分析来自 `refresh()` -> `finishBeanFactoryInitialization(beanFactory);` - >`preInstantiateSingletons()`

```java
@Override
public void preInstantiateSingletons() throws BeansException {
	if (logger.isTraceEnabled()) {
		logger.trace("Pre-instantiating singletons in " + this);
	}
    //迭代一个副本，允许init方法注册新的bean定义。
    //this.beanDefinitionNames 保存了所有的 beanNames
	List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);
	// 触发所有非延迟单例bean的初始化
	for (String beanName : beanNames) {
        //返回合并的RootBeanDefinition，遍历父bean定义
        //如果指定的bean对应于子bean定义。
        //这里其实也是一个重点，这里会有很多关于bean的类，后期我们会针对bean的类图进行讲解
		RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
		if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
            //在此处会根据beanName判断bean是不是一个FactoryBean，实现了FactoryBean接口的bean，会返回true
			if (isFactoryBean(beanName)) {
                // 然后通过getBean()方法去获取或者创建单例对象
                // 注意：在此处为beanName拼接了一个前缀：FACTORY_BEAN_PREFIX
                // FACTORY_BEAN_PREFIX是一个常量字符串，即：&
                // 所以在此时容器启动阶段，对于factoryBeanLearn，应该是：getBean("&factoryBeanLearn")
				Object bean = getBean(FACTORY_BEAN_PREFIX + beanName);
				if (bean instanceof FactoryBean) {
					final FactoryBean<?> factory = (FactoryBean<?>) bean;
					boolean isEagerInit;
                    //判断是否需要在容器启动阶段，就去实例化getObject()返回的对象，即是否调用FactoryBean的getObject()方法
					if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
						isEagerInit = AccessController.doPrivileged((PrivilegedAction<Boolean>)
										((SmartFactoryBean<?>) factory)::isEagerInit,
								getAccessControlContext());
					}
					else {
						isEagerInit = (factory instanceof SmartFactoryBean &&
								((SmartFactoryBean<?>) factory).isEagerInit());
					}
					if (isEagerInit) {
					    //重点方法
                        //返回指定bean的一个实例，该实例可以是共享的，也可以是独立的。
						getBean(beanName);
					}
				}
			}
			else {
				getBean(beanName);
			}
		}
	}
	// 为所有适用的bean触发初始化后回调
    // 到这里说明所有的非懒加载的 singleton beans 已经完成了初始化
    // 如果我们定义的 bean 是实现了 SmartInitializingSingleton 接口的，那么在这里得到回调，忽略
	for (String beanName : beanNames) {
		Object singletonInstance = getSingleton(beanName);
		if (singletonInstance instanceof SmartInitializingSingleton) {
			final SmartInitializingSingleton smartSingleton = (SmartInitializingSingleton) singletonInstance;
			if (System.getSecurityManager() != null) {
				AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
					smartSingleton.afterSingletonsInstantiated();
					return null;
				}, getAccessControlContext());
			}
			else {
				smartSingleton.afterSingletonsInstantiated();
			}
		}
	}
}
``` 
- 在容器启动阶段，会先通过getBean()方法来创建FactoryBeanLearn的实例对象。如果实现了SmartFactoryBean接口，
且isEagerInit()方法返回的是true，那么在容器启动阶段，就会调用getObject()方法，
向容器中注册getObject()方法返回值的对象。否则，只有当第一次获取getObject()返回值的对象时，才会去回调getObject()方法。
- 在getBean()中会调用到doGetBean()方法，下面为doGetBean()精简后的源码。从源码中我们发现，最终都会调用getObjectForBeanInstance()方法。

- getObjectForBeanInstance
```java
	protected Object getObjectForBeanInstance(
			Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) {

		// 如果不是一个 FactoryBean,则直接返回 
		// 如果是FactoryBean，且name是以&符号开头，那么表示的是获取FactoryBean的原生对象，也会直接返回
		if (BeanFactoryUtils.isFactoryDereference(name)) {
			if (beanInstance instanceof NullBean) {
				return beanInstance;
			}
			if (!(beanInstance instanceof FactoryBean)) {
				throw new BeanIsNotAFactoryException(beanName, beanInstance.getClass());
			}
			if (mbd != null) {
				mbd.isFactoryBean = true;
			}
			return beanInstance;
		}
		if (!(beanInstance instanceof FactoryBean)) {
			return beanInstance;
		}

		Object object = null;
		if (mbd != null) {
			mbd.isFactoryBean = true;
		}
		else {
		    //从缓存中获取。什么时候放入缓存的呢？在第一次调用getObject()方法时，会将返回值放入到缓存。
			object = getCachedObjectForFactoryBean(beanName);
		}
		if (object == null) {
			// Return bean instance from factory.
			FactoryBean<?> factory = (FactoryBean<?>) beanInstance;
			// 缓存从FactoryBean获取的对象（如果它是单例对象）
			if (mbd == null && containsBeanDefinition(beanName)) {
				mbd = getMergedLocalBeanDefinition(beanName);
			}
			boolean synthetic = (mbd != null && mbd.isSynthetic());
            //在getObjectFromFactoryBean()方法中最终会调用到getObject()方法
			object = getObjectFromFactoryBean(factory, beanName, !synthetic);
		}
		return object;
	}
```

- getObjectFromFactoryBean

```java
protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName, boolean shouldPostProcess) {
	// 如果BeanFactory的isSingleton()方法返回值是true,表示getObject()返回值对象是单例的
	if (factory.isSingleton() && containsSingleton(beanName)) {
		synchronized (getSingletonMutex()) {
			// 再一次判断缓存中是否存在。(双重检测机制，和平时写线程安全的代码类似)
			Object object = this.factoryBeanObjectCache.get(beanName);
			if (object == null) {
				// 在doGetObjectFromFactoryBean()中才是真正调用getObject()方法
				object = doGetObjectFromFactoryBean(factory, beanName);
				Object alreadyThere = this.factoryBeanObjectCache.get(beanName);
				if (alreadyThere != null) {
					object = alreadyThere;
				}
				else {
					// 下面是进行后置处理，和普通的bean的后置处理没有任何区别
					if (shouldPostProcess) {
						if (isSingletonCurrentlyInCreation(beanName)) {
							return object;
						}
						beforeSingletonCreation(beanName);
						try {
							object = postProcessObjectFromFactoryBean(object, beanName);
						}
						catch (Throwable ex) {
							throw new BeanCreationException(beanName,
									"Post-processing of FactoryBean's singleton object failed", ex);
						}
						finally {
							afterSingletonCreation(beanName);
						}
					}
					// 放入到缓存中
					if (containsSingleton(beanName)) {
						this.factoryBeanObjectCache.put(beanName, object);
					}
				}
			}
			return object;
		}
	}
	//非单例
	else {
		Object object = doGetObjectFromFactoryBean(factory, beanName);
		if (shouldPostProcess) {
			try {
				object = postProcessObjectFromFactoryBean(object, beanName);
			}
			catch (Throwable ex) {
				throw new BeanCreationException(beanName, "Post-processing of FactoryBean's object failed", ex);
			}
		}
		return object;
	}
}
```

- doGetObjectFromFactoryBean

```java
private Object doGetObjectFromFactoryBean(final FactoryBean<?> factory, final String beanName)
		throws BeanCreationException {

	Object object;
	try {
		if (System.getSecurityManager() != null) {
			AccessControlContext acc = getAccessControlContext();
			try {
				object = AccessController.doPrivileged((PrivilegedExceptionAction<Object>) factory::getObject, acc);
			}
			catch (PrivilegedActionException pae) {
				throw pae.getException();
			}
		}
		else {//直接调用 FactoryBean getObject()
			object = factory.getObject();
		}
	}
	catch (FactoryBeanNotInitializedException ex) {
		throw new BeanCurrentlyInCreationException(beanName, ex.toString());
	}
	catch (Throwable ex) {
		throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", ex);
	}
    //对于不完全的FactoryBean，不要接受空值
    //已初始化：许多factorybean只返回null。
	if (object == null) {
		if (isSingletonCurrentlyInCreation(beanName)) {
			throw new BeanCurrentlyInCreationException(
					beanName, "FactoryBean which is currently in creation returned null from getObject");
		}
		object = new NullBean();
	}
	return object;
}
```


