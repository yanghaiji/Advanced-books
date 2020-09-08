## SpringBoot 优雅停机

在服务部署后，当需要停机重新发布时，我们会执行 `kill -9 pid` ,也行你已经把这些重启的命令写成了脚本，但是最终都是强制的`kill` 掉进程，
如果这是有人在访问接口，如果你突然将服务`kill`掉，是不是会造成客户的体验感降低，这时候就需要优雅的停机，何为优雅的停机呢？不难理解的是
如果有请求正在执行，这时我们需要进行`kill`的时候，需要处理完现在正在执行的请求，并且不再接受新的请求，最后处理完现有的请求进行停机服务；
如何做到这些么，其实网上有很多讲解，但是本文主要讲解的是`SpringBoot2.3.0`后带来的新特性

### 开启优雅停机服务
在`SpringBoot2.3.0`后新增了如下特性,您只要将如下的代码添加到配置即可
```yaml
server:
  shutdown: graceful
```
`shutdown` 提供了两种配置模式，默认是`IMMEDIATE`,源码如下:
```java
public enum Shutdown {
	//优雅的停机服务
	GRACEFUL,
	//强制的关闭进程
	IMMEDIATE;
}
```

当然您还可以配置停机最大时间,默认30s
```yaml
spring:
  # 优雅停机宽限时间
  lifecycle:
    timeout-per-shutdown-phase: 50
```
[源码请点击这里](../../../source-code/src/main/java/com/javayh/advanced)
配置完成，话不多说，让我们写个接口测试一下：
为了达到验证效果，这里小编，将方法进行了`sleep`，
```java
@RequestMapping(value = "syslog")
public String test(){
    Thread.sleep(5000);
    return "test";
}
```
当我访问后，并立即关掉服务，这是你会发现日志如下:
```play
INFO 452 --- [extShutdownHook] o.s.b.w.e.tomcat.GracefulShutdown        : Commencing graceful shutdown. Waiting for active requests to 
INFO 452 --- [nio-9090-exec-1] c.j.advanced.spring.aop.SysLogAspect     : 当前访问访问次数 : 3
INFO 452 --- [nio-9090-exec-1] c.j.advanced.spring.aop.SysLogAspect     : 剩余访问访问次数 : 2
INFO 452 --- [nio-9090-exec-1] c.j.advanced.spring.aop.SysLogAspect     : 测试Aop注解执行耗时: 5008 s
INFO 452 --- [tomcat-shutdown] o.s.b.w.e.tomcat.GracefulShutdown        : Graceful shutdown complete
INFO 452 --- [extShutdownHook] o.s.s.concurrent.ThreadPoolTaskExecutor  : Shutting down ExecutorService 'applicationTaskExecutor'
```
可以发现我们的配置生效，并且也达到了，完成现有请求的处理，在进行服务停机
那么问题来了，我们为何配置了`server.shutdown=graceful`就可以实现我们想要的功能呢？

### graceful配置的原理

在之前我们分析`SpringBoot Tomcat`启动原理时,有如下的方法：
如果您不了解`SpringBoot Tomcat`启动原理，请先阅读下面文章，会对您的了解有更多的帮助
- [SpringBoot Tomcat 启动原理](ioc/SpringBoot_Tomcat.md)
- [SpringBoot Tomcat 获取配置文件](ioc/TomcatConfigurationFile.md)
```java
	@Override
	public WebServer getWebServer(ServletContextInitializer... initializers) {
		if (this.disableMBeanRegistry) {
			Registry.disableRegistry();
		}
		Tomcat tomcat = new Tomcat();
		File baseDir = (this.baseDirectory != null) ? this.baseDirectory : createTempDir("tomcat");
		tomcat.setBaseDir(baseDir.getAbsolutePath());
		Connector connector = new Connector(this.protocol);
		connector.setThrowOnFailure(true);
		tomcat.getService().addConnector(connector);
		customizeConnector(connector);
		tomcat.setConnector(connector);
		tomcat.getHost().setAutoDeploy(false);
		configureEngine(tomcat.getEngine());
		for (Connector additionalConnector : this.additionalTomcatConnectors) {
			tomcat.getService().addConnector(additionalConnector);
		}
	````	prepareContext(tomcat.getHost(), initializers);
		return getTomcatWebServer(tomcat);
	}
```
- getTomcatWebServer
```java
protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
	return new TomcatWebServer(tomcat, getPort() >= 0, getShutdown());
}

public TomcatWebServer(Tomcat tomcat, boolean autoStart, Shutdown shutdown) {
	Assert.notNull(tomcat, "Tomcat Server must not be null");
	this.tomcat = tomcat;
	this.autoStart = autoStart;
	this.gracefulShutdown = (shutdown == Shutdown.GRACEFUL) ? new GracefulShutdown(tomcat) : null;
	initialize();
}
```
这是我们启动时会自动读取配置，最终根据 `shutdown`的状态进行最终的执行；

但其实是由两种配置的，我们在回过头来看`createWebServer()`

```java
private void createWebServer() {
	WebServer webServer = this.webServer;
	ServletContext servletContext = getServletContext();
	if (webServer == null && servletContext == null) {
		ServletWebServerFactory factory = getWebServerFactory();
		this.webServer = factory.getWebServer(getSelfInitializer());
        // 1. 重点之处
		getBeanFactory().registerSingleton("webServerGracefulShutdown",
				new WebServerGracefulShutdownLifecycle(this.webServer));
		getBeanFactory().registerSingleton("webServerStartStop",
				new WebServerStartStopLifecycle(this, this.webServer));
	}
	else if (servletContext != null) {
		try {
			getSelfInitializer().onStartup(servletContext);
		}
		catch (ServletException ex) {
			throw new ApplicationContextException("Cannot initialize servlet context", ex);
		}
	}
	initPropertySources();
}
```
其实这里已经帮我进行了两种方式的停机处理，但是会根据最终的 `webServer` 进行处理

我查看 `webServer` 会发现

```java
default void shutDownGracefully(GracefulShutdownCallback callback) {
	callback.shutdownComplete(GracefulShutdownResult.IMMEDIATE);
}
```
而最终的处理就回到了根据
```java
@Override
public void shutDownGracefully(GracefulShutdownCallback callback) {
	if (this.gracefulShutdown == null) {
		callback.shutdownComplete(GracefulShutdownResult.IMMEDIATE);
		return;
	}
	this.gracefulShutdown.shutDownGracefully(callback);
}
```
这里只讲 本文相关的优雅停机方式展现出来：
```java
void shutDownGracefully(GracefulShutdownCallback callback) {
	logger.info("Commencing graceful shutdown. Waiting for active requests to complete");
	new Thread(() -> doShutdown(callback), "tomcat-shutdown").start();
}

private void doShutdown(GracefulShutdownCallback callback) {
	List<Connector> connectors = getConnectors();
	connectors.forEach(this::close);
	try {
		for (Container host : this.tomcat.getEngine().findChildren()) {
			for (Container context : host.findChildren()) {
				while (isActive(context)) {
					if (this.aborted) {
						logger.info("Graceful shutdown aborted with one or more requests still active");
						callback.shutdownComplete(GracefulShutdownResult.REQUESTS_ACTIVE);
						return;
					}
					Thread.sleep(50);
				}
			}
		}

	}
	catch (InterruptedException ex) {
		Thread.currentThread().interrupt();
	}
	logger.info("Graceful shutdown complete");
	callback.shutdownComplete(GracefulShutdownResult.IDLE);
}
```

不得不说Spring的源码真的时一环扣一环，环环相扣，哈哈哈!
