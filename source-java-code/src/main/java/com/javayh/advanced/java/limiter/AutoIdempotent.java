package com.javayh.advanced.java.limiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 限流注解
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-12-03 6:00 PM
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoIdempotent {

    String value() default "";

    long time() default 1000L;

}
