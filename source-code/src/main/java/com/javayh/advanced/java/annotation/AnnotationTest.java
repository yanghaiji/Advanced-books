package com.javayh.advanced.java.annotation;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <p>
 *
 * </p>
 *
 * @author Dylan-haiji
 * @version 1.0.0
 * @since 2020-07-17
 */
public class AnnotationTest {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationTest.class);

    public static void main(String[] args) {
        AddMapper addMapper = new AddMapper();
        //获取字节码文件对象
        Class<?> cls = addMapper.getClass();
        //获取方法
        Method[] methods = cls.getDeclaredMethods();
        for (Method method : methods) {
            //判断方法是否又此注解
            if (method.isAnnotationPresent(Limit.class)) {
                try {
                    //获取注解
                    Limit annotation = method.getAnnotation(Limit.class);
                    String value = annotation.value();
                    if (Strings.isBlank(value)) {
                        value = "Limit";
                    }
                    method.invoke(addMapper);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    logger.error(
                            "IllegalAccessException | InvocationTargetException---> {}", e.getMessage());
                }
            }
        }
    }
}
