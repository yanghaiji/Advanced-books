package com.javayh.advanced.spring.bean;

import com.javayh.advanced.spring.aop.SysLogAspect;
import com.javayh.advanced.spring.config.CustomConfigurationProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-27
 */
@SpringBootTest
public class BeanTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testBeanFactory(){
        // ApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        BeanFactory beanFactory = new AnnotationConfigApplicationContext(BeanService.class);
        BeanService bean = beanFactory.getBean(BeanService.class);
        System.out.println("bean name"+bean.getClass().getName());
    }

    /**
     * <p>
     *       测试 FactoryBean
     * </p>
     */
    @Test
    public void testFactoryBean() throws Exception {
        FactoryBeanLearn bean = applicationContext.getBean(FactoryBeanLearn.class);
        BaseBean object = bean.getObject();
        System.out.println(object);
    }
}
