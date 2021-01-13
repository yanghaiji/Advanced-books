package com.javayh.advanced.kafka.producer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * <p>
 *
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-12
 */
@Configuration
public class KafkaTopicConfig {

    @Value("${javayh.kafka.javayh}")
    private String topic1;

    @Value("${javayh.kafka.test}")
    private String topic2;

    @Bean
    NewTopic topic1() {
         //第一个是参数是topic名字，第二个参数是分区个数，第三个是topic的复制因子个数
        //当broker个数为1个时会创建topic失败，
        //提示：replication factor: 2 larger than available brokers: 1
        //只有在集群中才能使用kafka的备份功能
        //new NewTopic("foo", 10, (short) 2);
        return TopicBuilder.name(topic1).build();
    }

    @Bean
    NewTopic topic2() {
        return TopicBuilder.name(topic2).build();
    }

}
