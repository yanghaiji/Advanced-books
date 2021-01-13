package com.javayh.advanced.kafka.producer.web;

import com.javayh.advanced.kafka.producer.entity.User;
import com.javayh.advanced.kafka.producer.service.KafkaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 *
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-12
 */
@RestController
@RequestMapping("/kafka/")
public class KafkaController {
    @Resource
    private KafkaService kafkaService;

    @GetMapping(value = "test-01/{msg}")
    public void sendKafka(@PathVariable String msg){
        kafkaService.sendKafka("test",msg);
    }

    @GetMapping(value = "test-02")
    public void sendKafka(){
        User user = new User("java","123456");
        kafkaService.sendKafka("javayh",user);
    }

}
