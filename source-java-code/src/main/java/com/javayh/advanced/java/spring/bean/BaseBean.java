package com.javayh.advanced.java.spring.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 测试 factory bean
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-02
 */
@Builder
@Getter
@Setter
public class BaseBean {

    private String beanId;
    private String beanName;

    @Override
    public String toString() {
        return "{" +
                "beanId:'" + beanId + '\'' +
                ", beanName:'" + beanName + '\'' +
                '}';
    }

}
