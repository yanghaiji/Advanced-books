package com.javayh.advanced.consumer.consumer.listener;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.BatchAcknowledgingConsumerAwareMessageListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

/**
 * <p>
 *      	使用一种手动提交方法时，可使用此接口处理ConsumerRecord从Kafka使用者poll()操作接收到的所有实例。提供对对象的访问
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-15
 */
public class BatchAcknowledgingConsumerAware<K,V> implements BatchAcknowledgingConsumerAwareMessageListener<K,V> {
    @Override
    public void onMessage(List<ConsumerRecord<K, V>> list, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {

    }
}
