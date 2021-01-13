package com.javayh.advanced.kafka.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 *   producer 启动类
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-12
 */
@SpringBootApplication
public class KafkaProdApp {

    public static void main(String[] args) {
        SpringApplication.run(KafkaProdApp.class,args);
    }

}
