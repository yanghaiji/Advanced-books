package com.javayh.advanced;

import com.javayh.advanced.spring.config.CustomConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 *
 * </p>
 * @version 1.0.0
 * @author Dylan-haiji
 */
@Slf4j
@SpringBootApplication
public class AdvancedApplication {

    private final CustomConfigurationProperties customConfigurationProperties;

    public AdvancedApplication(CustomConfigurationProperties customConfigurationProperties) {
        this.customConfigurationProperties = customConfigurationProperties;
        log.info("customConfigurationProperties init : {}",customConfigurationProperties.toString());
    }

    public static void main(String[] args) {
        SpringApplication.run(AdvancedApplication.class, args);
    }

}
