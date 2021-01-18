package com.javayh.advanced.consumer.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * <p>
 * KafkaListener 用法演示
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-15
 */
public class KafkaListenerDemo {

    /**
     * 简单的使用
     * @param data 需要消费的数据
     */
    @KafkaListener(id = "foo", topics = "myTopic", clientIdPrefix = "myClientId")
    public void consumer(String data){

    }

    /**
     * 显式分区分配
     * @param record
     */
    @KafkaListener(id = "thing2", topicPartitions =
            { @TopicPartition(topic = "topic1", partitions = { "0", "1" }),
                    @TopicPartition(topic = "topic2", partitions = "0",
                            partitionOffsets = @PartitionOffset(partition = "1",initialOffset = "100"))
            })
    public void listen(ConsumerRecord<?, ?> record) {

    }

    /**
     * 该*通配符代表了所有分区partitions的属性。@PartitionOffset每个中只能有一个通配符@TopicPartition
     * @param record
     */
    @KafkaListener(id = "thing3", topicPartitions =
            { @TopicPartition(topic = "topic1", partitions = { "0", "1" },
                    partitionOffsets = @PartitionOffset(partition = "*", initialOffset = "0"))
            })
    public void listen2(ConsumerRecord<?, ?> record) {
    }


    /**
     * 手动提交
     * @param data
     * @param ack
     */
    @KafkaListener(id = "cat", topics = "myTopic",
            containerFactory = "kafkaManualAckListenerContainerFactory")
    public void listen(String data, Acknowledgment ack) {
        ack.acknowledge();
    }

    @KafkaListener(id = "qux", topicPattern = "myTopic1")
    public void listen(@Payload String foo,
                       @Header(name = KafkaHeaders.RECEIVED_MESSAGE_KEY, required = false) Integer key,
                       @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts) {

    }
}
