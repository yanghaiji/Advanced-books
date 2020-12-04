package com.javayh.advanced.java.patterns.factory;

import com.javayh.advanced.exception.ServerException;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-14
 */
public class JettyWebServerFactory<T> implements ServerFactory<T> {
    @Override
    public T create(int port) {
        return null;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {

    }
}
