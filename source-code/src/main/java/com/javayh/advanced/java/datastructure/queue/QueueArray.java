package com.javayh.advanced.java.datastructure.queue;

import com.javayh.advanced.exception.ExceptionQueueEmpty;
import com.javayh.advanced.exception.ExceptionQueueFull;

/**
 * <p>
 * 队列实现
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-20
 */
public class QueueArray<E> implements Queue<E> {
    transient Object[] elementData;

    //默认长度
    transient static final int DEFAULT_CAPACITY = 512;

    //实际容量
    protected int capacity;

    //队首的位置
    private int fist = 0;

    //队尾元素的位置
    private int last = 0;

    public QueueArray() {
        this(DEFAULT_CAPACITY);
    }

    public QueueArray(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        capacity = initialCapacity;
        this.elementData = new Object[initialCapacity];

    }


    @Override
    public int size() {
        return (capacity - fist + last) % capacity;
    }

    @Override
    public boolean isEmpty() {
        return fist == last;
    }

    @Override
    public E front() throws ExceptionQueueEmpty {
        assertEmpty();
        return elementData(fist);
    }

    @Override
    public void enqueue(E e) throws ExceptionQueueFull {
        if (size() == capacity - 1) {
            throw new ExceptionQueueFull("Queue overflow.");
        }
        elementData[last] = e;
        last = (last + 1) % capacity;
    }

    @Override
    public E dequeue() throws ExceptionQueueEmpty {
        E element;
        assertEmpty();
        element = elementData(fist);
        elementData[fist] = null;
        fist = (fist + 1) % capacity;
        return element;
    }

    protected void assertEmpty() {
        if (isEmpty()) {
            throw new ExceptionQueueEmpty("意外：队列空");
        }
    }

    E elementData(int index) {
        return (E) elementData[index];
    }
}
