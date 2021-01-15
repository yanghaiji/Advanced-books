package com.javayh.advanced.kafka.producer.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 *      自定义kafka拦截器
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-14
 */
@Slf4j
@Configuration
public class MyKafkaInterceptor implements ProducerInterceptor<String, String> {

    private AtomicInteger successCounter = new AtomicInteger(0);
    private AtomicInteger errorCounter = new AtomicInteger(0);

    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        // 创建一个新的 record ，把时间戳写入消息体的最前部
        return new ProducerRecord(record.topic(),
                                record.partition(), record.timestamp(),
                                record.key(),
                           System.currentTimeMillis() + "," +
                                record.value().toString());
    }

    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {
        // 统计成功和失败的次数
        if (e == null) {
            successCounter.incrementAndGet();
        } else{
            errorCounter.decrementAndGet();
        }
    }

    /**
     * 只有关闭producer 服务才会触发，所以这里最好进行持久化存储
     */
    @Override
    public void close() {
        log.info("successCounter 为 [{}]",successCounter);
        log.info("errorCounter 为 [{}]",errorCounter);
    }

    @Override
    public void configure(Map<String, ?> map) {

    }

}
