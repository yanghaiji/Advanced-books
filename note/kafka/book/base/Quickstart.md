## 服务搭建与测试命令

本教程假定您是一只小白，没有Kafka 或ZooKeeper 方面的经验。 Kafka控制脚本在Unix和Windows平台有所不同，在Windows平台，请使用 `bin\windows\` 而不是`bin/`, 并将脚本扩展名改为`.bat`.

#### Step 1: 下载代码

[下载](https://kafka.apache.org/downloads) 选择您所需要的版本并解压缩.

```
> tar -xzf kafka_2.12-2.7.0.tgz 
> cd kafka_2.12-2.7.0
```
##### 常用配置介绍
```
broker 的 全局唯一编号，不能重复
broker.id=0
删除 topic 功能使能
delete.topic.enable=true
处理网络请求 的 线程数量
num.network.threads=3
用来 处理磁盘 IO 的现成数量
num.io.threads=8
发送套接字的缓冲区大小
socket.send.buffer.bytes=102400
接收套接字的缓冲区大小
socket.receive.buffer.bytes=102400
请求套接字的缓冲区大小
socket.request.max.bytes=104857600
kafka 运行日志存放的路径
log.dirs=/opt/module/kafka/logs
topic 在当前 broker 上的分区个数
num.partitions=1
用来恢复和清理 data 下数据的线程数量
num.recovery.threads.per.data.dir=1
segment 文件保留的最长时间，超时将被删除
log.retention.hours=168
配置连接 Zookeeper 集群 地址
zookeeper.connect=localhost:2181,localhost:2181,localhost:21 81
```
##### 当然您也可以将kafka配置到环境变量里
```
> vi /etc/profile

    #KAFKA_HOME
    export KAFKA_HOME=/opt/module/kafka
    export PATH=$PATH:$KAFKA_HOME/bin

> source /etc/profile
```

#### Step 2: 启动服务器

Kafka 使用 [ZooKeeper](https://zookeeper.apache.org/) 如果你还没有ZooKeeper服务器，你需要先启动一个ZooKeeper服务器。
 您可以通过与kafka打包在一起的便捷脚本来快速简单地创建一个单节点ZooKeeper实例。

```
> bin/zookeeper-server-start.sh config/zookeeper.properties

# .\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
```

现在启动Kafka服务器：

```
> bin/kafka-server-start.sh config/server.properties

#.\bin\windows\kafka-server-start.bat .\config\server.properties
```

#### Step 3: 创建一个 topic

让我们创建一个名为“hello-kafak”的topic，它有一个分区和一个副本：

```
> bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic hello-kafak

#.\bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic hello-kafka
```
选项说明：
- topic 定义 topic名
- replication-factor 定义副本数
- partitions 定义分区数
现在我们可以运行list（列表）命令来查看这个topic：

```
> bin/kafka-topics.sh --list --zookeeper localhost:2181 hello-kafak
```

或者，您也可将代理配置为：在发布的topic不存在时，自动创建topic，而不是手动创建。

如果您想删除topic,可以通过一下命令进行删除
```
 bin/kafka topics.sh --zookeeper:2181 --delete --topic first
```
需要`server.properties`中设置 `delete.topic.enable=true`否则只是标记删除。

#### Step 4: 发送一些消息

Kafka自带一个命令行客户端，它从文件或标准输入中获取输入，并将其作为message（消息）发送到Kafka集群。默认情况下，每行将作为单独的message发送。

运行 producer，然后在控制台输入一些消息以发送到服务器。

```
> bin/kafka-console-producer.sh --broker-list localhost:9092 --topic hello-kafak 

>hello
>kafak
```

#### Step 5: 启动一个 consumer

Kafka 还有一个命令行consumer（消费者），将消息转储到标准输出。

```
> bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic hello-kafak --from-beginning 
hello
kafak
```
注:`--from-beginning `表示从起始的最大偏移开始消费，如果不加则从当前的offset开始消费

如果您将上述命令在不同的终端中运行，那么现在就可以将消息输入到生产者终端中，并将它们在消费终端中显示出来。

所有的命令行工具都有其他选项；运行不带任何参数的命令将显示更加详细的使用信息。

#### Step 6: 设置多代理集群

到目前为止，我们一直在使用单个代理，这并不好玩。对 Kafka来说，单个代理只是一个大小为一的集群，除了启动更多的代理实例外，没有什么变化。
 为了深入了解它，让我们把集群扩展到三个节点（仍然在本地机器上）。

首先，为每个代理创建一个配置文件 (在Windows上使用`copy` 命令来代替)：

```
> cp config/server.properties config/server-1.properties
> cp config/server.properties config/server-2.properties
```

现在编辑这些新文件并设置如下属性：

```
config/server-1.properties:
    broker.id=1  
    listeners=PLAINTEXT://:9093  
    log.dir=/kafka-logs-1 
config/server-2.properties:
    broker.id=2
    listeners=PLAINTEXT://:9094
    log.dir=/kafka-logs-2
```

`broker.id`属性是集群中每个节点的名称，这一名称是唯一且永久的。
我们必须重写端口和日志目录，因为我们在同一台机器上运行这些，我们不希望所有的代理尝试在同一个端口注册，或者覆盖彼此的数据。

我们已经建立Zookeeper和一个单节点了，现在我们只需要启动两个新的节点：

```
> bin/kafka-server-start.sh config /server-1.properties &

> bin/kafka-server-start.sh config/server-2.properties &

```

现在创建一个副本为3的新topic：

```
> bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 3 --partitions 1 --topic my-replicated-topic
```

Good，现在我们有一个集群，但是我们怎么才能知道那些代理在做什么呢？运行"describe topics"命令来查看：

```
> bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic my-replicated-topic 
Topic:my-replicated-topic  PartitionCount:1  ReplicationFactor:3 Configs:
Topic: my-replicated-topic Partition: 0  Leader: 1  Replicas: 1,2,0 Isr: 1,2,0
```

以下是对输出信息的解释。第一行给出了所有分区的摘要，下面的每行都给出了一个分区的信息。因为我们只有一个分区，所以只有一行。

- “leader”是负责给定分区所有读写操作的节点。每个节点都是随机选择的部分分区的领导者。
- “replicas”是复制分区日志的节点列表，不管这些节点是leader还是仅仅活着。
- “isr”是一组“同步”replicas，是replicas列表的子集，它活着并被指到leader。

请注意，在示例中，节点1是该主题中唯一分区的领导者。

我们可以在已创建的原始主题上运行相同的命令来查看它的位置：

```
> bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic hello-kafka 
Topic: hello-kafka  PartitionCount:1  ReplicationFactor:1 Configs:
Topic: hello-kafka  `Partition: 0  Leader: 0  Replicas: 0 Isr: 0
```

这没什么大不了，原来的主题没有副本且在服务器0上。我们创建集群时，这是唯一的服务器。

让我们发表一些信息给我们的新topic：

```
> bin/kafka-console-producer.sh --broker-list localhost:9092 --topic my-replicated-topic
my test message 1
my test message 2
```

现在我们来消费这些消息：

```
> bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --from-beginning --topic my-replicated-topic
my test message 1
my test message 2
```

让我们来测试一下容错性。 Broker 1 现在是 leader，让我们来杀了它：

```
> ps aux | grep server-1.properties
7564 ttys002    0:15.91 /System/Library/Frameworks/JavaVM.framework/Versions/1.8/Home/bin/java...
> kill -9 7564
```

在 Windows 上用:

```
> wmic process where "caption = 'java.exe' and commandline like '%server-1.properties%'" get processid
ProcessId
6016
> taskkill /pid 6016 /f
```

领导权已经切换到一个从属节点，而且节点1也不在同步副本集中了：

```
> bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic my-replicated-topic
Topic:my-replicated-topic   PartitionCount:1    ReplicationFactor:3 Configs:
    Topic: my-replicated-topic  Partition: 0    Leader: 2   Replicas: 1,2,0 Isr: 2,0
```

不过，即便原先写入消息的leader已经不在，这些消息仍可用于消费：

```
> bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --from-beginning --topic my-replicated-topic
...
my test message 1
my test message 2
```

#### Step 7: 使用Kafka Connect来导入/导出数据

从控制台读出数据并将其写回是十分方便操作的，但你可能需要使用其他来源的数据或将数据从Kafka导出到其他系统。针对这些系统， 你可以使用Kafka Connect来导入或导出数据，而不是写自定义的集成代码。

Kafka Connect是Kafka的一个工具，它可以将数据导入和导出到Kafka。它是一种可扩展工具，通过运行*connectors（连接器）*， 使用自定义逻辑来实现与外部系统的交互。 在本文中，我们将看到如何使用简单的connectors来运行Kafka Connect，这些connectors 将文件中的数据导入到Kafka topic中，并从中导出数据到一个文件。

首先，我们将创建一些种子数据来进行测试：

```
> echo -e "foo\nbar" > test.txt
```

在Windows系统使用:

```
> echo foo> test.txt
> echo bar>> test.txt
```

接下来，我们将启动两个*standalone（独立）*运行的连接器，这意味着它们各自运行在一个单独的本地专用 进程上。 我们提供三个配置文件。首先是Kafka Connect的配置文件，包含常用的配置，如Kafka brokers连接方式和数据的序列化格式。 其余的配置文件均指定一个要创建的连接器。这些文件包括连接器的唯一名称，类的实例，以及其他连接器所需的配置。

```
> bin/connect-standalone.sh config/connect-standalone.properties config/connect-file-source.properties config/connect-file-sink.properties
```

这些包含在Kafka中的示例配置文件使用您之前启动的默认本地群集配置，并创建两个连接器： 第一个是源连接器，用于从输入文件读取行，并将其输入到 Kafka topic。 第二个是接收器连接器，它从Kafka topic中读取消息，并在输出文件中生成一行。

在启动过程中，你会看到一些日志消息，包括一些连接器正在实例化的指示。 一旦Kafka Connect进程启动，源连接器就开始从` test.txt `读取行并且 将它们生产到主题` connect-test `中，同时接收器连接器也开始从主题` connect-test `中读取消息， 并将它们写入文件` test.sink.txt `中。我们可以通过检查输出文件的内容来验证数据是否已通过整个pipeline进行交付：

```
> more test.sink.txt
foo
bar
```

请注意，数据存储在Kafka topic` connect-test `中，因此我们也可以运行一个console consumer（控制台消费者）来查看 topic 中的数据（或使用custom consumer（自定义消费者）代码进行处理）：

```
> bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic connect-test --from-beginning
{"schema":{"type":"string","optional":false},"payload":"foo"}
{"schema":{"type":"string","optional":false},"payload":"bar"}
...
```

连接器一直在处理数据，所以我们可以将数据添加到文件中，并看到它在pipeline 中移动：

```
> > echo Another line>> test.txt
```

您应该可以看到这一行出现在控制台用户输出和接收器文件中。

#### Step 8:使用 Kafka Streams 来处理数据

Kafka Streams是用于构建实时关键应用程序和微服务的客户端库，输入与输出数据存储在Kafka集群中。 
Kafka Streams把客户端能够轻便地编写部署标准Java和Scala应用程序的优势与Kafka服务器端集群技术相结合，使这些应用程序具有高度伸缩性、弹性、容错性、分布式等特性。 