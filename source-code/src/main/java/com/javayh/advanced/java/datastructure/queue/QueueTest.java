package com.javayh.advanced.java.datastructure.queue;

import com.javayh.advanced.exception.ExceptionQueueFull;


/**
 * <p>
 *      测试队列
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-20
 */
public class QueueTest {

    public static void main(String[] args) throws ExceptionQueueFull {
        Queue<String> queue = new QueueArray<>(16);
        queue.enqueue("haiji");
        queue.enqueue("yang");
        queue.enqueue("haiji");
        queue.dequeue();
        queue.enqueue("haiji");
        queue.enqueue("yang");
        queue.enqueue("haiji");
    }
}
