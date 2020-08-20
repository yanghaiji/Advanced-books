package com.javayh.advanced.java.datastructure.queue;

import com.javayh.advanced.exception.ExceptionQueueEmpty;
import com.javayh.advanced.exception.ExceptionQueueFull;

/**
 * <p>
 * 自定义队列
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-20
 */
public interface Queue<E> {

    /**
     * 返回队列的长度
     * @return
     */
    int size();

    /**
     * 判断队列是否为空
     * @return
     */
    boolean isEmpty();

    /**
     * 获取队列第一个元素，但是不删除
     * @return
     * @throws ExceptionQueueEmpty
     */
    E front() throws ExceptionQueueEmpty;

    /**
     * 入队
     * @param e
     * @throws ExceptionQueueFull
     */
    void enqueue (E e) throws ExceptionQueueFull;

    /**
     * 出队
     * @return
     * @throws ExceptionQueueEmpty
     */
    E dequeue() throws ExceptionQueueEmpty;
}
