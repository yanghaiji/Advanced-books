package com.javayh.advanced.kafka.producer.service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.javayh.advanced.kafka.producer.entity.User;
import com.javayh.advanced.kafka.producer.util.KafkaSend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Service;

/**
 * <p>
 *
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-12
 */
@Service
public class KafkaService {

    private final KafkaSend kafkaSend;
    public KafkaService(KafkaSend kafkaSend) {
        this.kafkaSend = kafkaSend;
    }

    public void sendKafka(String topic , String msg){
        kafkaSend.send(topic,msg);
    }

    public void sendKafka(String topic , User msg){
        kafkaSend.send(topic,msg);
    }
}
