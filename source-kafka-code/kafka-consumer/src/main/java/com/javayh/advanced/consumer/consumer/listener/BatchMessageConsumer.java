package com.javayh.advanced.consumer.consumer.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.BatchMessageListener;

import java.util.List;

/**
 * <p>
 *      使用自动提交或容器管理的提交方法之一时，可使用此接口处理ConsumerRecord从Kafka使用者poll()操作接收的所有实例。
 * 使用此接口时不支持，因为已为侦听器提供了完整的批处理。AckMode.RECORD
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-15
 */
public class BatchMessageConsumer<K,V> implements BatchMessageListener<K,V> {
    @Override
    public void onMessage(List<ConsumerRecord<K, V>> consumerRecords) {

    }
}
