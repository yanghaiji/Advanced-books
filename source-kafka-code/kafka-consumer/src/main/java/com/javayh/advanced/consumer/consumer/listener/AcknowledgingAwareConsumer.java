package com.javayh.advanced.consumer.consumer.listener;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.AcknowledgingConsumerAwareMessageListener;
import org.springframework.kafka.support.Acknowledgment;

/**
 * <p>
 *      用此接口来处理ConsumerRecord从Kafka使用者poll()操作接收的单个实例
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-15
 */
public class AcknowledgingAwareConsumer<K,V> implements AcknowledgingConsumerAwareMessageListener<K,V> {

    @Override
    public void onMessage(ConsumerRecord<K, V> consumerRecord,
                          Acknowledgment acknowledgment, Consumer<?, ?> consumer) {

    }
}
