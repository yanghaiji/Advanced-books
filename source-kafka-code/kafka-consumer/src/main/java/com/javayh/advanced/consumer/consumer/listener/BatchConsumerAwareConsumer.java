package com.javayh.advanced.consumer.consumer.listener;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.BatchConsumerAwareMessageListener;

import java.util.List;

/**
 * <p>
 *  使用此接口时不支持，因为已为侦听器提供了完整的批处理。提供对对象的访问。AckMode.RECORD
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-15
 */
public class BatchConsumerAwareConsumer<K,V> implements BatchConsumerAwareMessageListener<K,V> {
    @Override
    public void onMessage(List<ConsumerRecord<K, V>> list, Consumer<?, ?> consumer) {

    }
}
