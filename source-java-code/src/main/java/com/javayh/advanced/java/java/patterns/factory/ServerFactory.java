package com.javayh.advanced.java.java.patterns.factory;

import com.javayh.advanced.java.exception.ServerException;

import java.util.Objects;

/**
 * <p>
 * 容器启动的工厂
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-14
 */
public interface ServerFactory<T> {

    /**
     * <p>
     * 创建
     * </p>
     *
     * @param port
     * @return void
     */
    T create(int port);

    /**
     * <p>
     * 启动容器
     * </p>
     *
     * @param
     * @return void
     */
    void start();

    /**
     * <p>
     * 定制容器
     * </p>
     *
     * @param
     * @return void
     */
    void stop();

    /**
     * <p>
     * 销毁容器
     * </p>
     *
     * @param
     * @return void
     */
    void destroy();

    /**
     * 判断容器是否创建成功
     *
     * @param bean
     * @param <T>
     * @throws ServerException
     */
    static <T> void isNull(T bean) throws ServerException {
        if (Objects.isNull(bean)) {
            throw new ServerException("create server exception");
        }
    }

    /**
     * <p>
     * 同于
     * </p>
     *
     * @param
     * @return void
     */
    default void after() {
        this.stop();
        this.destroy();
    }

}
