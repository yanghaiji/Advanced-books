## SpringApplication 运行及源码解读

### SpringApplication.run

```java
@SpringBootApplication
public class AdvancedApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdvancedApplication.class, args);
    }
}
```
在使用SpringBoot时,上面的代码我们一定很熟悉,这样写我们的程序就会自动创建并允许,但是Spring具体为我们做了什么我们就不得而知,
接下来我们就来阅读一下源码来一探究竟

#### 源码分析

对于这段代码官方的解释是: `Run the Spring application, creating and refreshing a new` 大意为:运行Spring应用程序，创建并刷新一个新的
```java
public ConfigurableApplicationContext run(String... args) {
    //1.
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    ConfigurableApplicationContext context = null;
    Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
    //2.
    configureHeadlessProperty();
    //3.
    SpringApplicationRunListeners listeners = getRunListeners(args);
    listeners.starting();
    try {
    	ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
        //4.
    	ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
        //5.
    	configureIgnoreBeanInfo(environment);
    	Banner printedBanner = printBanner(environment);
        //6.
    	context = createApplicationContext();
        //7.
    	exceptionReporters = getSpringFactoriesInstances(SpringBootExceptionReporter.class,
    			new Class[] { ConfigurableApplicationContext.class }, context);
        //8.
    	prepareContext(context, environment, listeners, applicationArguments, printedBanner);
        //9.
    	refreshContext(context);
    	afterRefresh(context, applicationArguments);
    	stopWatch.stop();
    	if (this.logStartupInfo) {
    		new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), stopWatch);
    	}
    	listeners.started(context);
    	callRunners(context, applicationArguments);
    }
    catch (Throwable ex) {
    	handleRunFailure(context, ex, exceptionReporters, listeners);
    	throw new IllegalStateException(ex);
    }

    try {
    	listeners.running(context);
    }
    catch (Throwable ex) {
    	handleRunFailure(context, ex, exceptionReporters, null);
    	throw new IllegalStateException(ex);
    }
	return context;
}
```

- 1.`stopWatch.start();` 这里只是为了记录创建完成启动Spring所需要的时间,如：`Started XXXApplication in 3.82 seconds (JVM running for 6.336)`
- 2.通过`configureHeadlessProperty()`配置属性
```java
private void configureHeadlessProperty() {
	System.setProperty(SYSTEM_PROPERTY_JAVA_AWT_HEADLESS,
			System.getProperty(SYSTEM_PROPERTY_JAVA_AWT_HEADLESS, Boolean.toString(this.headless)));
}
```
- 3.通过`getRunListeners()`实例化监听器
```java
	private SpringApplicationRunListeners getRunListeners(String[] args) {
		Class<?>[] types = new Class<?>[] { SpringApplication.class, String[].class };
		return new SpringApplicationRunListeners(logger,
				getSpringFactoriesInstances(SpringApplicationRunListener.class, types, this, args));
	}
```
这里请注意看 `SpringApplicationRunListeners` 与 `SpringApplicationRunListener`,这时连个类,前者是后者的一个集合，
这时我们需在看`getSpringFactoriesInstances()`方法
```java
	private <T> Collection<T> getSpringFactoriesInstances(Class<T> type, Class<?>[] parameterTypes, Object... args) {
		ClassLoader classLoader = getClassLoader();
		// Use names and ensure unique to protect against duplicates
		Set<String> names = new LinkedHashSet<>(SpringFactoriesLoader.loadFactoryNames(type, classLoader));
		List<T> instances = createSpringFactoriesInstances(type, parameterTypes, classLoader, args, names);
		AnnotationAwareOrderComparator.sort(instances);
		return instances;
	}
```
如果您看过 [EnableAutoConfiguration 源码解析](EnableAutoConfiguration.md) ,您很快就会明白他做了什么，这里主要是`SpringFactoriesLoader.loadFactoryNames`
是将`spring.factories`文件中的key存放在List中,在通过`createSpringFactoriesInstances`实例化
通过`spring.factories` 我们可以看出最终实例化的是 `EventPublishingRunListener`, 然后在进行 `listeners.starting();`来发送事件,
这里的`starting()`是调用的`EventPublishingRunListener`,当我查看其内部方法是,在实例化时又会预加载了`ApplicationListener`
spring.factories 文件
```properties
# Run Listeners
org.springframework.boot.SpringApplicationRunListener=\
org.springframework.boot.context.event.EventPublishingRunListener

# Application Listeners
org.springframework.context.ApplicationListener=\
org.springframework.boot.ClearCachesApplicationListener,\
org.springframework.boot.builder.ParentContextCloserApplicationListener,\
org.springframework.boot.cloud.CloudFoundryVcapEnvironmentPostProcessor,\
org.springframework.boot.context.FileEncodingApplicationListener,\
org.springframework.boot.context.config.AnsiOutputApplicationListener,\
org.springframework.boot.context.config.ConfigFileApplicationListener,\
org.springframework.boot.context.config.DelegatingApplicationListener,\
org.springframework.boot.context.logging.ClasspathLoggingApplicationListener,\
org.springframework.boot.context.logging.LoggingApplicationListener,\
org.springframework.boot.liquibase.LiquibaseServiceLocatorApplicationListener
```
- 4.prepareEnvironment 配置环境
```java
    private ConfigurableEnvironment prepareEnvironment(SpringApplicationRunListeners listeners,
    		ApplicationArguments applicationArguments) {
    	// 1
    	ConfigurableEnvironment environment = getOrCreateEnvironment();
    	configureEnvironment(environment, applicationArguments.getSourceArgs());
    	ConfigurationPropertySources.attach(environment);
        //2
    	listeners.environmentPrepared(environment);
    	bindToSpringApplication(environment);
    	if (!this.isCustomEnvironment) {
    		environment = new EnvironmentConverter(getClassLoader()).convertEnvironmentIfNecessary(environment,
    				deduceEnvironmentClass());
    	}
    	ConfigurationPropertySources.attach(environment);
    	return environment;
    }
```
首先会通过(1)`getOrCreateEnvironment()`等方法创建和配置环境，(2)发布一个`ApplicationEnvironmentPreparedEvent`监听,代码如下,具体发布的内容，大家可以追踪一下
```java
    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
    	this.initialMulticaster
    			.multicastEvent(new ApplicationEnvironmentPreparedEvent(this.application, this.args, environment));
    }
```
`bindToSpringApplication`进行了对`spring.main`的绑定，完成配置环境

- 5.通过`configureIgnoreBeanInfo(environment);` `Banner printedBanner = printBanner(environment)`配置忽略一些bean，以及启动时的Banner
- 6.创建`ConfigurableApplicationContext`
- 7.`getSpringFactoriesInstances` 我们发现最终又是通过`loadSpringFactories()`方法读取`spring.factories`文件
- 8.接下来我们继续查看`prepareContext()`
```java
    private void prepareContext(ConfigurableApplicationContext context, ConfigurableEnvironment environment,
    		SpringApplicationRunListeners listeners, ApplicationArguments applicationArguments, Banner printedBanner) {
    	context.setEnvironment(environment);
    	postProcessApplicationContext(context);
    	applyInitializers(context);
    	listeners.contextPrepared(context);
    	if (this.logStartupInfo) {
    		logStartupInfo(context.getParent() == null);
    		logStartupProfileInfo(context);
    	}
    	// 1. Add boot specific singleton beans
    	ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
    	beanFactory.registerSingleton("springApplicationArguments", applicationArguments);
    	if (printedBanner != null) {
    		beanFactory.registerSingleton("springBootBanner", printedBanner);
    	}
    	if (beanFactory instanceof DefaultListableBeanFactory) {
    		((DefaultListableBeanFactory) beanFactory)
    				.setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
    	}
    	if (this.lazyInitialization) {
    		context.addBeanFactoryPostProcessor(new LazyInitializationBeanFactoryPostProcessor());
    	}
    	// 1. Load the sources
    	Set<Object> sources = getAllSources();
    	Assert.notEmpty(sources, "Sources must not be empty");
    	load(context, sources.toArray(new Object[0]));
    	listeners.contextLoaded(context);
    }
```
首先配置一个环境信息,之后会通过`isteners.contextPrepared(context);`发送`ApplicationContextInitializedEvent`
```java
	@Override
	public void contextPrepared(ConfigurableApplicationContext context) {
		this.initialMulticaster
				.multicastEvent(new ApplicationContextInitializedEvent(this.application, this.args, context));
	}
```
接下获取启动类，并将其作为bean加载到Spring 容器中,再通过 `listeners.contextLoaded(context);` 发送 `ApplicationPreparedEvent`

- 9.` refresh() `方法，我们追踪代码会发现，最终走的是`AbstractApplicationContext`下方的代码
这个方法做了很多，如: 完成`beanFactory`设置, invoke `ProcessBeanFactory` ,国际化信息等,关于refresh()更多信息,请点击这里[refresh源码解析](refresh.md)
```java
    public void refresh() throws BeansException, IllegalStateException {
        synchronized(this.startupShutdownMonitor) {
            //准备刷新此上下文
            this.prepareRefresh();
            //告诉子类刷新内部bean工厂
            ConfigurableListableBeanFactory beanFactory = this.obtainFreshBeanFactory();
            //准备BeanFactory以供在此上下文中使用
            this.prepareBeanFactory(beanFactory);
            try {
                //允许在上下文子类中对bean工厂进行后处理
                this.postProcessBeanFactory(beanFactory);
                //在上下文中调用注册为bean的工厂处理器
                this.invokeBeanFactoryPostProcessors(beanFactory);
                //注册拦截bean创建的bean处理器
                this.registerBeanPostProcessors(beanFactory);
                //初始化此上下文的消息源
                this.initMessageSource();
                //为此上下文初始化事件多宿主
                this.initApplicationEventMulticaster();
                //初始化特定上下文子类中的其他特殊bean
                this.onRefresh();
                //检查侦听器bean并注册它们
                this.registerListeners();
                //实例化所有剩余的（非延迟初始化）单例
                this.finishBeanFactoryInitialization(beanFactory);
                //最后一步：发布对应事件
                this.finishRefresh();
            } catch (BeansException var9) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Exception encountered during context initialization - cancelling refresh attempt: " + var9);
                }
                //销毁已创建的单实例以避免悬空资源
                this.destroyBeans();
                //重置“活动”标志
                this.cancelRefresh(var9);
                //将异常传播到调用方
                throw var9;
            } finally {
                //重新设置Spring核心中的常见内省缓存，因为我们
                //可能不再需要单例bean的元数据了
                this.resetCommonCaches();
            }
        }
    }
```
到这SpringBoot项目就完车了启动,最后`callRunners()`方法,这是我们经常用到的,当项目启动后区做一些处理，获取实现`ApplicationRunner` `CommandLineRunner`这两个接口

```java
	private void callRunners(ApplicationContext context, ApplicationArguments args) {
		List<Object> runners = new ArrayList<>();
		runners.addAll(context.getBeansOfType(ApplicationRunner.class).values());
		runners.addAll(context.getBeansOfType(CommandLineRunner.class).values());
		AnnotationAwareOrderComparator.sort(runners);
		for (Object runner : new LinkedHashSet<>(runners)) {
			if (runner instanceof ApplicationRunner) {
				callRunner((ApplicationRunner) runner, args);
			}
			if (runner instanceof CommandLineRunner) {
				callRunner((CommandLineRunner) runner, args);
			}
		}
	}
```

