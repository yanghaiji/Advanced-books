package com.javayh.advanced.consumer.consumer.listener;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.ConsumerAwareMessageListener;

/**
 * <p>
 *      使用自动提交或容器管理的提交方法之一时，可使用此接口处理ConsumerRecord从Kafka使用者poll()操作接收的单个实例。
 *   提供对对象的访问。
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-15
 */
public class ConsumerAware<K,V> implements ConsumerAwareMessageListener<K,V> {
    @Override
    public void onMessage(ConsumerRecord<K, V> consumerRecord, Consumer<?, ?> consumer) {

    }
}
