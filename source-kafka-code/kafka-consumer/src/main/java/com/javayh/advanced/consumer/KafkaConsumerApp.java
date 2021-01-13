package com.javayh.advanced.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * <p>
 * consumer启动类
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-12
 */
@EnableKafka
@SpringBootApplication
public class KafkaConsumerApp {

    public static void main(String[] args) {
        SpringApplication.run(KafkaConsumerApp.class,args);
    }
}
