package com.javayh.advanced.spring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author haiyang
 */
@Component
@ConfigurationProperties(prefix = "javayh", ignoreUnknownFields = true)
public class CustomConfigurationProperties {
    private String author;
    private String gender;
    private Integer age;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "{" +
                "author:'" + author + '\'' +
                ", gender:'" + gender + '\'' +
                ", age:'" + age + '\'' +
                '}';
    }
}
