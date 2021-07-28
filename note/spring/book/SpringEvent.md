## Spring 中标准事件与自定义事件

### 简介

`ApplicationContext`是通过`ApplicationEvent` 类和`ApplicationListener`接口提供的。如果将实现`ApplicationListener`接口的 bean 部署到上下文中，则每次 `ApplicationEvent`将 发布到 时`ApplicationContext`，都会通知该 bean。本质上，这是标准的观察者设计模式。、

从 Spring 4.2 开始，事件基础结构得到了显着改进，并提供了基于注释的模型以及发布任意事件（即不一定从 扩展的对象`ApplicationEvent`）的能力。当此类对象发布时，我们会为您将其包装在一个事件中。

下表描述了 Spring 提供的标准事件：

| 事件                         | 解释                                                         |
| :--------------------------- | :----------------------------------------------------------- |
| `ContextRefreshedEvent`      | 在`ApplicationContext`初始化或刷新时发布（例如，通过使用接口`refresh()`上的方法`ConfigurableApplicationContext`）。在这里，“初始化”意味着所有 bean 都被加载，后处理器 bean 被检测并激活，单例被预实例化，并且`ApplicationContext`对象准备好使用。只要上下文尚未关闭，就可以多次触发刷新，前提是所选对象`ApplicationContext`实际上支持这种“热”刷新。例如，`XmlWebApplicationContext`支持热刷新，但`GenericApplicationContext`不支持 。 |
| `ContextStartedEvent`        | `ApplicationContext`使用界面`start()`上的方法 启动时发布`ConfigurableApplicationContext`。在这里，“已启动”意味着所有`Lifecycle` bean 都收到一个明确的启动信号。通常，此信号用于在显式停止后重新启动 bean，但它也可用于启动尚未配置为自动启动的组件（例如，尚未在初始化时启动的组件）。 |
| `ContextStoppedEvent`        | `ApplicationContext`使用界面`stop()`上的方法 停止时发布`ConfigurableApplicationContext`。这里，“停止”意味着所有`Lifecycle` bean 都收到一个明确的停止信号。停止的上下文可以通过`start()`调用重新启动 。 |
| `ContextClosedEvent`         | 在`ApplicationContext`使用接口`close()`上的方法`ConfigurableApplicationContext`或通过 JVM 关闭挂钩关闭时发布。在这里，“关闭”意味着所有的单例 bean 都将被销毁。一旦上下文关闭，它就会到达生命的尽头，无法刷新或重新启动。 |
| `RequestHandledEvent`        | 一个特定于 Web 的事件，告诉所有 bean 已为 HTTP 请求提供服务。此事件在请求完成后发布。此事件仅适用于使用 Spring 的`DispatcherServlet`. |
| `ServletRequestHandledEvent` | 它的一个子类`RequestHandledEvent`添加了特定于 Servlet 的上下文信息。 |

您还可以创建和发布自己的自定义事件。以下示例显示了一个扩展 Spring`ApplicationEvent`基类的简单类：

```java
public class BlockedListEvent extends ApplicationEvent {

    private final String address;
    private final String content;

    public BlockedListEvent(Object source, String address, String content) {
        super(source);
        this.address = address;
        this.content = content;
    }

    // accessor and other methods...
}
```

要发布自定义`ApplicationEvent`，调用`publishEvent()`上的方法 `ApplicationEventPublisher`。通常，这是通过创建一个实现`ApplicationEventPublisherAware`并将其注册为 Spring bean的类来完成的 。以下示例显示了这样一个类：

```java
public class EmailService implements ApplicationEventPublisherAware {

    private List<String> blockedList;
    private ApplicationEventPublisher publisher;

    public void setBlockedList(List<String> blockedList) {
        this.blockedList = blockedList;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void sendEmail(String address, String content) {
        if (blockedList.contains(address)) {
            publisher.publishEvent(new BlockedListEvent(this, address, content));
            return;
        }
        // send email...
    }
}
```

在配置时，Spring 容器会检测到`EmailService`实现 `ApplicationEventPublisherAware`并自动调用 `setApplicationEventPublisher()`. 实际上，传入的参数是Spring容器本身。您正在通过其`ApplicationEventPublisher`界面与应用程序上下文进行 交互。

要接收 custom `ApplicationEvent`，您可以创建一个实现 `ApplicationListener`并将其注册为 Spring bean 的类。以下示例显示了这样一个类：

```
public class BlockedListNotifier implements ApplicationListener<BlockedListEvent> {

    private String notificationAddress;

    public void setNotificationAddress(String notificationAddress) {
        this.notificationAddress = notificationAddress;
    }

    public void onApplicationEvent(BlockedListEvent event) {
        // notify appropriate parties via notificationAddress...
    }
}
```

请注意，`ApplicationListener`它通常使用自定义事件的类型进行参数化（`BlockedListEvent`在前面的示例中）。这意味着该 `onApplicationEvent()`方法可以保持类型安全，避免任何向下转型的需要。您可以根据需要注册任意数量的事件侦听器，但请注意，默认情况下，事件侦听器会同步接收事件。这意味着该`publishEvent()`方法会阻塞，直到所有侦听器都完成对事件的处理。这种同步和单线程方法的一个优点是，当侦听器接收到事件时，如果事务上下文可用，它就会在发布者的事务上下文中运行。



总而言之，当调用 bean的`sendEmail()`方法时`emailService`，如果有任何电子邮件消息应该被阻止，`BlockedListEvent`则会发布一个自定义事件类型 。该`blockedListNotifier`bean被注册为 `ApplicationListener`接收的`BlockedListEvent`，在这一点上，可以通知有关各方。

 Spring 的事件机制是为同一应用程序上下文中 Spring bean 之间的简单通信而设计的。然而，对于更复杂的企业集成需求，单独维护的 Spring Integration项目为构建基于众所周知的 Spring 编程模型的轻量级、面向模式、事件驱动的架构提供了完整的支持 。

### 基于注解的事件监听器

您可以使用`@EventListener`注释在托管 bean 的任何方法上注册事件侦听器 。该`BlockedListNotifier`可改写如下：

```java
public class BlockedListNotifier {

    private String notificationAddress;

    public void setNotificationAddress(String notificationAddress) {
        this.notificationAddress = notificationAddress;
    }

    @EventListener
    public void processBlockedListEvent(BlockedListEvent event) {
        // notify appropriate parties via notificationAddress...
    }
}
```

方法签名再次声明了它侦听的事件类型，但这次使用了灵活的名称并且没有实现特定的侦听器接口。只要实际事件类型在其实现层次结构中解析您的泛型参数，也可以通过泛型缩小事件类型。

如果您的方法应该侦听多个事件，或者您想定义它时根本不带参数，则还可以在注释本身上指定事件类型。以下示例显示了如何执行此操作：

```java
@EventListener({ContextStartedEvent.class, ContextRefreshedEvent.class})
public void handleContextStart() {
    // ...
}
```

还可以通过使用`condition`定义`SpEL`表达式的注释的属性添加额外的运行时过滤，该属性应该匹配以实际调用特定事件的方法。

以下示例显示了如何重写我们的通知程序，使其仅`content`在事件的属性等于时才被调用 `my-event`：

```java
@EventListener(condition = "#blEvent.content == 'my-event'")
public void processBlockedListEvent(BlockedListEvent blEvent) {
    // notify appropriate parties via notificationAddress...
}
```

如果您需要发布一个事件作为处理另一个事件的结果，您可以更改方法签名以返回应该发布的事件，如以下示例所示：

```java
@EventListener
public ListUpdateEvent handleBlockedListEvent(BlockedListEvent event) {
    // notify appropriate parties via notificationAddress and
    // then publish a ListUpdateEvent...
}
```

项目实战代码地址[https://github.com/yanghaiji/IT-Demo](https://github.com/yanghaiji/IT-Demo)