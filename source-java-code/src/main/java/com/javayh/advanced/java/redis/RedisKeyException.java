package com.javayh.advanced.java.redis;

/**
 * <p>
 *
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-12-17 5:23 PM
 */
public class RedisKeyException extends RuntimeException{

    public RedisKeyException() {
    }

    public RedisKeyException(String message) {
        super(message);
    }
}
