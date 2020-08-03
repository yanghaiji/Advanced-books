## SpringBoot 为什么帮我们开启了自动配置类


之前我们自定义一个AOP时,都需要添加 `@EnableAspectJAutoProxy`,但是为何在SpringBoot项目中,我们只需要引入依赖就可以完整自动的配置呢？
**答：**因为AOP的自动配置类帮我们开启了

为何会自动开启呢?在[EnableAutoConfiguration 源码分析](EnableAutoConfiguration.md)时,我们说过这样一段代码，也是Spring源码中经常用到的；
```java
private static Map<String, List<String>> loadSpringFactories(@Nullable ClassLoader classLoader) {
   MultiValueMap<String, String> result = (MultiValueMap)cache.get(classLoader);
   if (result != null) {
       return result;
   } else {
       try {
           Enumeration<URL> urls = classLoader != null ? classLoader.getResources("META-INF/spring.factories") : ClassLoader.getSystemResources("META-INF/spring.factories");
           LinkedMultiValueMap result = new LinkedMultiValueMap();

           ......

           cache.put(classLoader, result);
           return result;
       } catch (IOException var13) {
           throw new IllegalArgumentException("Unable to load factories from location [META-INF/spring.factories]", var13);
       }
   }

}
```
其实它是将`spring.factories`文件中的key存放在List中,而在这里您会发现，如下的配置
```properties
# Auto Configure
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration,\
org.springframework.boot.autoconfigure.aop.AopAutoConfiguration,
```
也就是说Spring启动时会，自动将`AopAutoConfiguration`注入到容器中，那我们看一下`AopAutoConfiguration`的源码，他做了写什么

```java
@Configuration(proxyBeanMethods = false)
//解析spring.aop.auto=true属性 加载aspect配置
@ConditionalOnProperty(prefix = "spring.aop", name = "auto", havingValue = "true", matchIfMissing = true)
public class AopAutoConfiguration {

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(Advice.class)
	static class AspectJAutoProxyingConfiguration {

		@Configuration(proxyBeanMethods = false)
		@EnableAspectJAutoProxy(proxyTargetClass = false)
        //spring.aop.proxy-target-class = false 采用jdk动态代理
		@ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "false",
				matchIfMissing = false)
		static class JdkDynamicAutoProxyConfiguration {

		}

		@Configuration(proxyBeanMethods = false)
		@EnableAspectJAutoProxy(proxyTargetClass = true)
        // spring.aop.proxy-target-class = true 采用cglib动态代理
		@ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "true",
				matchIfMissing = true)
		static class CglibAutoProxyConfiguration {

		}

	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnMissingClass("org.aspectj.weaver.Advice")
    // spring.aop.proxy-target-class = true 开启动态代理
	@ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "true",
			matchIfMissing = true)
	static class ClassProxyingConfiguration {

		ClassProxyingConfiguration(BeanFactory beanFactory) {
			if (beanFactory instanceof BeanDefinitionRegistry) {
				BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
				AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
				AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
			}
		}

	}

}
```
**说明:**
>@ConditionalOnClass(Advice.class)
>        –> 在当前的类路径下存在EnableAspectJAutoProxy.class, Aspect.class, Advice.class时该配置才被解析
>@ConditionalOnProperty(prefix = "spring.aop", name = "auto", havingValue = "true", matchIfMissing = true)
>        –> 当配置有spring.aop.auto= true时生效.如果没有配置,则默认生效

因此,我们可以知道,aop 默认使用的是Cglib代理，Spring官方是这样说的
`Whether subclass-based (CGLIB) proxies are to be created (true), as opposed to standard Java interface-based proxies (false).`
这是针对于`spring.aop.proxy-target-class`的配置进行说明的，如果您想使用JDK代理，只需要修改默认的配置即可