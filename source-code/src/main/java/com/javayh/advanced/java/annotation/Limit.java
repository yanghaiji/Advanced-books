package com.javayh.advanced.java.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 自定义注解
 * </p>
 *
 * @author Dylan-haiji
 * @version 1.0.0
 * @since 2020-07-17
 */
@Documented
@Target(value= {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Limit
{
    String value();
}

