package com.javayh.advanced.consumer.consumer.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

/**
 * <p>
 *      使用自动提交或容器管理的提交方法之一时，可使用此接口处理ConsumerRecord从Kafka使用者poll()操作接收的单个实例。
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-15
 */
public class MessageListenerConsumer<K, V> implements MessageListener<K, V> {

    @Override
    public void onMessage(ConsumerRecord<K, V> kvConsumerRecord) {

    }

}
