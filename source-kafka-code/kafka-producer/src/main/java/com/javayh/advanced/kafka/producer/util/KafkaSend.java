package com.javayh.advanced.kafka.producer.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.RoutingKafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
public class KafkaSend {
    
    private final KafkaTemplate kafkaTemplate;

    public KafkaSend(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String kafkaTopic,Object message) {
        kafkaTemplate.send(kafkaTopic, message);
    }

    public void send(String kafkaTopic,String key ,Object message) {
        kafkaTemplate.send(kafkaTopic, key,message);
    }

    public void sendCallback(String topicName,Object message) {
        log.info("Sending : {}", message);
        log.info("---------------------------------");
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicName, message);
        callback(message, future);
    }

    public void sendCallback(String kafkaTopic,String key ,Object message) {
        log.info("Sending : {}", message);
        log.info("---------------------------------");
        ListenableFuture<SendResult<String, Object>> future =   kafkaTemplate.send(kafkaTopic, key,message);
        callback(message, future);
    }


    private void callback(Object message, ListenableFuture<SendResult<String, Object>> future) {
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                log.info("Success Callback: [{}] delivered with offset -{}", message,
                        result.getRecordMetadata().offset());
            }
            @Override
            public void onFailure(Throwable ex) {
                log.warn("Failure Callback: Unable to deliver message [{}]. {}", message, ex.getMessage());
            }
        });
    }

}
