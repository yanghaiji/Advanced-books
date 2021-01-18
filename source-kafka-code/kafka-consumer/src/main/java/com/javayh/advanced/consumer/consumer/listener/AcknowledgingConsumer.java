package com.javayh.advanced.consumer.consumer.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

/**
 * <p>
 *      使用手动提交方法之一时，可使用此接口来处理ConsumerRecord从Kafka使用者poll()操作接收的单个实例。
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-15
 */
public class AcknowledgingConsumer<K,V> implements AcknowledgingMessageListener<K,V> {

    @Override
    public void onMessage(ConsumerRecord<K, V> consumerRecord, Acknowledgment acknowledgment) {

    }
}
