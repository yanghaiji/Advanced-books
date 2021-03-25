## Seata 与 SpringBoot 集成 实现2PC模式的分布式事务

[Seata 分布式事务实践和开源详解](https://blog.csdn.net/weixin_38937840/article/details/115176502)

[Seata 部署](https://github.com/yanghaiji/Advanced-books/blob/master/note/transaction/README.md)

[项目的源码](https://github.com/yanghaiji/javayh-demo/tree/main/source-seata-code)

### 1.添加依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>source-seata-code</artifactId>
        <groupId>com.javayh.advanced</groupId>
        <version>1.0.0.RELEASE</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <description>springboot mybatis seata 集成demo</description>
    <artifactId>boot-mybatis-seata-code</artifactId>
    <packaging>pom</packaging>
    <modules>
        <module>seata-prod-code</module>
        <module>seata-coms-code</module>
    </modules>
    <properties>
        <spring-cloud-dependencies.version>Hoxton.SR8</spring-cloud-dependencies.version>
        <spring-cloud-alibaba-dependencies.version>2.2.5.RELEASE</spring-cloud-alibaba-dependencies.version>
    </properties>
    <dependencies>
        <!--<dependency>
            <groupId>io.seata</groupId>
            <artifactId>seata-spring-boot-starter</artifactId>
            <version>1.4.1</version>
        </dependency>-->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.1.3</version>
        </dependency>
        <!-- Spring Cloud Nacos Service Discovery -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <!-- Spring Cloud Nacos Config -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
        </dependency>
        <!-- Spring Cloud Seata -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
        </dependency>
    </dependencies>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <!--spring cloud-->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud-dependencies.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <!--alibaba cloud-->
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>${spring-cloud-alibaba-dependencies.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

### 2.创建两个服务进行模拟

- 创建两个服务
  - seata-prod-code	
  - seata-coms-code
- 创建两个数据库
  - 导入init.sql
  - undo_log两个数据库需要都导入
  - 其他的两张表一个数据库一个

### 3.代码编写

- 服务的配置

  - 这个服务每个服务都需要添加

  ```yaml
  server:
    port: 9012
  spring:
    application:
      name: boot-seata-app-coms
    mvc:
      throw-exception-if-no-handler-found: true
    main:
      allow-bean-definition-overriding: true
    cloud:
      # nacos 配置
      nacos:
        config:
          enabled: true
          namespace: 97447291-3fc6-44bd-8572-27f359df652e
          server-addr: 127.0.0.1:8848
        discovery:
          namespace: 97447291-3fc6-44bd-8572-27f359df652e
          server-addr: 127.0.0.1:8848
          #设置注册的ip地址
          ip: 127.0.0.1
          #取值范围 1 到 100，数值越大，权重越大
          weight: 5
    datasource:
  #    type: io.seata.rm.datasource.DataSourceProxy
      type: com.alibaba.druid.pool.DruidDataSource
      driverClassName: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/dalao?serverTimezone=CTT&characterEncoding=utf8&autoReconnect=true&useUnicode=true&useSSL=true
      username: root
      password: root
  #    hikari:
  #      minimum-idle: 10
  #      maximum-pool-size: 30
  #      auto-commit: true
  #      idle-timeout: 30000
  #      pool-name: DemoHikari
  #      max-lifetime: 60000
  #      connection-timeout: 60000
  #      validation-timeout: 5000
  #      read-only: false
  #      login-timeout: 5
  ## mybatis 常用配置
  mybatis:
    ### xml存放路径
    mapper-locations: classpath*:mapper/*/*Mapper.xml
    configuration:
      cache-enabled: true
      lazy-loading-enabled: false
      aggressive-lazy-loading: true
      log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  
  seata:
    enabled: true
    application-id: ${spring.application.name}
    tx-service-group: my_test_tx_group
    #enable-auto-data-source-proxy: true
    #use-jdk-proxy: false
    client:
      rm:
        async-commit-buffer-limit: 1000
        report-retry-count: 5
        table-meta-check-enable: false
        report-success-enable: false
        lock:
          retry-interval: 10
          retry-times: 30
          retry-policy-branch-rollback-on-conflict: true
      tm:
        commit-retry-count: 5
        rollback-retry-count: 5
      undo:
        data-validation: true
        log-serialization: jackson
        log-table: undo_log
      log:
        exceptionRate: 100
    service:
      vgroup-mapping:
        my_test_tx_group: default
      grouplist:
        default: 127.0.0.1:8091
      #enable-degrade: false
      #disable-global-transaction: false
    transport:
      shutdown:
        wait: 3
      thread-factory:
        boss-thread-prefix: NettyBoss
        worker-thread-prefix: NettyServerNIOWorker
        server-executor-thread-prefix: NettyServerBizHandler
        share-boss-worker: false
        client-selector-thread-prefix: NettyClientSelector
        client-selector-thread-size: 1
        client-worker-thread-prefix: NettyClientWorkerThread
        worker-thread-size: default
        boss-thread-size: 1
      type: TCP
      server: NIO
      heartbeat: true
      serialization: seata
      compressor: none
      enable-client-batch-send-request: true
  #  config:
  #    type: file
  #    consul:
  #      server-addr: 127.0.0.1:8500
  #    apollo:
  #      apollo-meta: http://192.168.1.204:8801
  #      app-id: seata-server
  #      namespace: application
  #    etcd3:
  #      server-addr: http://localhost:2379
  #    nacos:
  #      namespace:
  #      serverAddr: localhost
  #      group: SEATA_GROUP
  #    zk:
  #      server-addr: 127.0.0.1:2181
  #      session-timeout: 6000
  #      connect-timeout: 2000
  #      username: ""
  #      password: ""
    registry:
      type: file
      consul:
        cluster: default
        server-addr: 127.0.0.1:8500
  #    etcd3:
  #      cluster: default
  #      serverAddr: http://localhost:2379
  #    eureka:
  #      application: default
  #      weight: 1
  #      service-url: http://localhost:8761/eureka
  #    nacos:
  #      cluster: default
  #      server-addr: localhost
  #      namespace:
  #    redis:
  #      server-addr: localhost:6379
  #      db: 0
  #      password:
  #      cluster: default
  #      timeout: 0
  #    sofa:
  #      server-addr: 127.0.0.1:9603
  #      application: default
  #      region: DEFAULT_ZONE
  #      datacenter: DefaultDataCenter
  #      cluster: default
  #      group: SEATA_GROUP
  #      addressWaitTime: 3000
  #    zk:
  #      cluster: default
  #      server-addr: 127.0.0.1:2181
  #      session-timeout: 6000
  #      connect-timeout: 2000
  #      username: ""
  #      password: ""
  ```

- 业务代码编写

  - prod

    ```java
    @Slf4j
    @Service
    public class GoodsService {
        @Autowired
        private ComsClient comsClient;
    
        @Resource
        private GoodsDao goodsDao;
    
        /**
         *
         * @param account
         */
        @Transactional(rollbackFor = Exception.class)
        @GlobalTransactional(rollbackFor = Exception.class)
        public void updateAccount(int account){
            log.info("开启全局事务 id 为 {}", RootContext.getXID());
            goodsDao.updateAccount(account);
            //远程调用
            String goods = comsClient.goods(account);
            if("fallback".equalsIgnoreCase(goods)){
                throw new RuntimeException("服务调用异常");
            }
            if (10 == account){
                throw new RuntimeException("人为制造异常");
            }
    
        }
    
    }
    ```

  - coms

    ```java
    @Slf4j
    @Service
    public class GoodsService {
    
        @Resource
        private GoodsDao goodsDao;
    
        /**
         *
         * @param account
         */
        //@GlobalTransactional(rollbackFor = Exception.class)
        @Transactional(rollbackFor = Exception.class)
        public String updateAccount(int account){
            log.info("开启全局事务 id 为 {}", RootContext.getXID());
            goodsDao.updateAccount(account);
            if(account == 5){
                //throw new RuntimeException("测试失败回滚");
                return "1";
            }
            return "0";
        }
    }
    ```

- 启动服务进行测试

  - 下载 Nacos 本文使用的是1.4.1
    - [Nacos 安装与使用说明](https://github.com/yanghaiji/Advanced-books/blob/master/note/nacos/README.md)
  - 下载 Seata 本文使用的是1.4.1
    - [Seata安装与使用说明](https://github.com/yanghaiji/Advanced-books/blob/master/note/transaction/README.md)
  - 创建两个数据库并将init.sql导入
  - undo_log创建到prod 项目所连接的数据库
  - [`Seata Demo 工程 下载`](https://github.com/yanghaiji/javayh-demo/tree/main/source-seata-code)

  - 启动` prod 、coms` 项目
    - 访问  [GET http://localhost:9011/goods/10](http://localhost:9011/goods/10)