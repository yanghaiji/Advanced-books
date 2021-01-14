package com.javayh.advanced.kafka.producer.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Map;

/**
 * <p>
 * kafka发送工具
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-12
 */
@Slf4j
@Configuration
public class KafkaSend<K,V> {

    private final KafkaTemplate<K,V> kafkaTemplate;

    public KafkaSend(KafkaTemplate<K,V> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 冲洗生产者。
     */
    public void flush() {
        kafkaTemplate.flush();
    }

    /**
     * 使用提供的键和分区将数据发送到提供的主题。
     * @param topic         主题
     * @param partition     分区
     * @param timestamp     记录的时间戳。
     * @param key           密钥
     * @param data          数据
     */
    public void send(String topic, Integer partition, Long timestamp, K key, V data) {
        callback(kafkaTemplate.send(topic,partition,timestamp,key,data),data);
    }

    /**
     * 使用提供的密钥（没有分区）将数据发送到提供的主题。
     * @param topic 主题
     * @param key   密钥
     * @param data  数据
     */
    public void send(String topic,K key ,V data) {
        callback(kafkaTemplate.send(topic, key,data),data);
    }

    /**
     * 没有密钥或分区，将数据发送到提供的主题。
     * @param topic 主题
     * @param data  数据
     */
    public void send(String topic,V data) {
        callback(kafkaTemplate.send(topic, data),data);
    }

    /**
     * 发送提供的ProducerRecord
     * @param data
     */
    public void send(ProducerRecord<K,V> data) {
        callback(kafkaTemplate.send(data),data);
    }

    /**
     * 发送带有消息头中的路由信息​​的消息。消息有效负载可以在发送之前进行转换。
     * @param data
     */
    public void send(Message<V> data) {
        callback(kafkaTemplate.send(data),data);
    }

    /**
     * 在事务中运行时，将使用者偏移量发送到事务。组ID是从获取的 KafkaUtils.getConsumerGroupId()。
     * 如果在侦听器容器线程上调用了这些操作（并且侦听器容器配置有KafkaAwareTransactionManager），则不必调用此方法， 因为容器将负责将偏移量发送到事务。
     * @param data
     */
    public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> data) {
        kafkaTemplate.sendOffsetsToTransaction(data);
    }

    /**
     * 在事务中运行时，将使用者偏移量发送到事务。
     * 如果在侦听器容器线程上调用了这些操作（并且侦听器容器配置有KafkaAwareTransactionManager），则不必调用此方法， 因为容器将负责将偏移量发送到事务。
     * @param data
     * @param consumerGroupId
     */
    public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> data,
                                         String consumerGroupId) {
        kafkaTemplate.sendOffsetsToTransaction(data,consumerGroupId);
    }

    private void callback(ListenableFuture<SendResult<K, V>> future, Object data) {
        log.info("Sending : {}", data);
        log.info("---------------------------------");
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<K, V> result) {
                log.info("Success Callback: [{}] delivered with offset -{}", data,
                        result.getRecordMetadata().offset());
            }
            @Override
            public void onFailure(Throwable ex) {
                log.warn("Failure Callback: Unable to deliver message [{}]. {}", data, ex.getMessage());
            }
        });
    }

}
