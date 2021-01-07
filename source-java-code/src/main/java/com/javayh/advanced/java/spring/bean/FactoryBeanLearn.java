package com.javayh.advanced.java.spring.bean;

import com.javayh.advanced.java.spring.config.CustomConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-02
 */
@Configuration
public class FactoryBeanLearn implements FactoryBean<BaseBean>, InitializingBean {

    Logger log = LoggerFactory.getLogger(FactoryBeanLearn.class);

    private BaseBean baseBean;

    private final CustomConfigurationProperties customConfigurationProperties;

    public FactoryBeanLearn(CustomConfigurationProperties customConfigurationProperties) {
        this.customConfigurationProperties = customConfigurationProperties;
        log.info("customConfigurationProperties init : {}", customConfigurationProperties.toString());
    }

    @Override
    public BaseBean getObject() throws Exception {
        if (Objects.isNull(baseBean)) {
            this.afterPropertiesSet();
        }
        return this.baseBean;
    }

    @Override
    public Class<?> getObjectType() {
        return BaseBean.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("实例化 FactoryBeanLearn");
        this.baseBean = this.buildBaseBean();
    }

    /**
     * <p>
     * 实例化对象
     * </p>
     *
     * @param
     * @return com.javayh.advanced.spring.bean.BaseBean
     */
    private BaseBean buildBaseBean() {
        if (Objects.isNull(customConfigurationProperties)) {
            throw new RuntimeException("customConfigurationProperties is null");
        }
        return BaseBean.builder()
                .beanId(customConfigurationProperties.getGender())
                .beanName(customConfigurationProperties.getAuthor()).build();
    }

}
