## 自定义kafka Interceptor

### 拦截器原理

Producer拦截器 (interceptor)是在 Kafka 0.10版本被引入的，主要用于实现 clients端的定
制化控制逻辑。对于producer而言， interceptor使得用户在消息发送前以及 producer回调逻辑前有机会
对消息做一些定制化需求，比如 修改消息 等。同时， producer允许用户指定多个 interceptor按序作用于同一条消息从而形成一个拦截链 (interceptor chain)。 Intercetpor的实现接口是
org.apache.kafka.clients.producer.ProducerInterceptor，其 定义的方法包括：

1 configure(configs)

获取配置信息 和 初始化数据时调用 。

2 onSend() 

该方法封装进KafkaProducer.send方法中，即它运行在用户主线程中。 
Producer确保在消息被序列化以及计算分区前调用该方法。用户可以在该方法中对消息做任何操作，
但最好保证不要修改消息所属的 topic和分区， 否则会影响目标分区的计算 。

3 onAcknowledgement(RecordMetadata,Exception)

该方法会在消息 从 RecordAccumulator成功 发送到 Kafka Broker之后，或者在发送过程
  中失败时调用。 并且通常都是在 producer回调逻辑触发之前。 onAcknowledgement运行在
  producer的 IO线程中，因此不要在该方法中放入很重的逻辑，否则会拖慢 producer的消息
  发送效率 。
  
4 close

关闭 interceptor，主要用于执行一些资源清理工作如前所述，
interceptor可能被运行在多个线程中，因此在具体实现时用户需要自行确保
线程安全。另外 倘若指定了多个 interceptor，则 producer将按照指定顺序调用它们 ，并仅仅
是捕获每个 interceptor可能抛出的异常记录到错误日志中而非在向上传递。这在使用过程中
要特别留意。  

### 自定义拦截器

```java
@Slf4j
@Configuration
public class MyKafkaInterceptor implements ProducerInterceptor<String, String> {

    private AtomicInteger successCounter = new AtomicInteger(0);
    private AtomicInteger errorCounter = new AtomicInteger(0);

    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        // 创建一个新的 record ，把时间戳写入消息体的最前部
        return new ProducerRecord(record.topic(),
                                record.partition(), record.timestamp(),
                                record.key(),
                           System.currentTimeMillis() + "," +
                                record.value().toString());
    }

    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {
        // 统计成功和失败的次数
        if (e == null) {
            successCounter.incrementAndGet();
        } else{
            errorCounter.decrementAndGet();
        }
    }

    /**
     * 只有关闭producer 服务才会触发，所以这里最好进行持久化存储
     */
    @Override
    public void close() {
        log.info("successCounter 为 [{}]",successCounter);
        log.info("errorCounter 为 [{}]",errorCounter);
    }

    @Override
    public void configure(Map<String, ?> map) {

    }

}
```