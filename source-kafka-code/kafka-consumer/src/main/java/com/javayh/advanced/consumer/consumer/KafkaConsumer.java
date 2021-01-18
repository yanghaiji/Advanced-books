package com.javayh.advanced.consumer.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * <p>
 *
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-12
 */
@Slf4j
@Component
public class KafkaConsumer {

    /**
     *  消费者
     * @param record 消息
     */
    @Transactional(rollbackFor = Exception.class)
    @KafkaListener(topics = "test",groupId = "javayh-kafka")
    public void processMessage(ConsumerRecord<String, Object> record) {
        print(record);
    }

    @Transactional(rollbackFor = Exception.class)
    @KafkaListener(topics = "javayh",id = "javayh-kafka")
    public void processJson(ConsumerRecord<String, Object> record) {
        print(record);
    }

    void print(ConsumerRecord<String, Object> record){
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("开始消费");
            log.info("record {}",record);
            log.info("message {}", message);
        }
    }
}
