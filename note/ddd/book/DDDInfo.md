## DDD 简介

领域驱动设计(DDD) 是一种通过将实现连接到持续进化的模型来满足复杂需求的软件开发方法. 领域驱动设计的前提是:

- 把项目的主要重点放在核心领域和领域逻辑上
- 把复杂的设计放在领域模型上
- 发起技术专家和领域专家之间的创造性协作,以迭代方式完善解决特定领域问题的概念模型

更多的介绍可以参考[维基百科中的定义](https://zh.wikipedia.org/wiki/%E9%A0%98%E5%9F%9F%E9%A9%85%E5%8B%95%E8%A8%AD%E8%A8%88)

### 分层

.NET中的ABP框架遵循DDD原则和模式去实现分层应用程序模型,该模型由四个基本层组成:

- 表示层: 为用户提供接口. 使用应用层实现与用户交互.
- 应用层: 表示层与领域层的中介,编排业务对象执行特定的应用程序任务. 使用应用程序逻辑实现用例.
- 领域层: 包含业务对象以及业务规则. 是应用程序的核心.
- 基础设施层: 提供通用的技术功能,支持更高的层,主要使用第三方类库.

这与Java的分层很相似，但是目前大家的开发中，基本上用的都是 `Controller -> Service -> Dao`,
而DDD的思想不难看出，就是在我们现有的三层结构中添加了一个领域层。

- 表示层{interfaces}
  - 接口服务位于用户接口层，用于处理用户发送的Restful请求和解析用户输入的配置文件等，并将信息传递给应用层。
- 应用层{application}
  - 应用服务位于应用层。用来表述应用和用户行为，负责服务的组合、编排和转发，负责处理业务用例的执行顺序以及结果的拼装。
  - 应用层的服务包括应用服务和领域事件相关服务。
  - 应用服务可对微服务内的领域服务以及微服务外的应用服务进行组合和编排，或者对基础层如文件、缓存等数据直接操作形成应用服务，对外提供粗粒度的服务。
  - 领域事件服务包括两类：领域事件的发布和订阅。通过事件总线和消息队列实现异步数据传输，实现微服务之间的解耦。
- 领域层{domain}
  - 领域服务位于领域层，为完成领域中跨实体或值对象的操作转换而封装的服务，领域服务以与实体和值对象相同的方式参与实施过程。
  - 领域服务对同一个实体的一个或多个方法进行组合和封装，或对多个不同实体的操作进行组合或编排，对外暴露成领域服务。领域服务封装了核心的业务逻辑。实体自身的行为在实体类内部实现，向上封装成领域服务暴露。
  - 为隐藏领域层的业务逻辑实现，所有领域方法和服务等均须通过领域服务对外暴露。
  - 为实现微服务内聚合之间的解耦，原则上禁止跨聚合的领域服务调用和跨聚合的数据相互关联。
- 基础层{infrastructrue}
  - 基础服务位于基础层。为各层提供资源服务（如数据库、缓存等），实现各层的解耦，降低外部资源变化对业务逻辑的影响。
  - 基础服务主要为仓储服务，通过依赖反转的方式为各层提供基础资源服务，领域服务和应用服务调用仓储服务接口，利用仓储实现持久化数据对象或直接访问基础资源。



