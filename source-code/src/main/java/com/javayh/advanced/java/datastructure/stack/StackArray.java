package com.javayh.advanced.java.datastructure.stack;

import com.javayh.advanced.exception.ExceptionStackEmpty;
import com.javayh.advanced.exception.ExceptionStackFull;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-19
 */
public class StackArray<E> implements Stack<E> {

    transient Object[] elementData;

    //默认长度
    transient static final int DEFAULT_CAPACITY = 512;

    //实际容量
    protected int capacity;

    //栈顶元素的位置
    protected int top = -1;

    public StackArray() {
        this(DEFAULT_CAPACITY);
    }

    public StackArray(int initialCapacity) {
        if (initialCapacity > 0) {
            capacity = initialCapacity;
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            capacity = DEFAULT_CAPACITY;
            this.elementData = new Object[DEFAULT_CAPACITY];
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
    }

    /**
     * 入栈
     *
     * @param e
     */
    @Override
    public void push(E e) {
        if (size() == capacity) {
            throw new ExceptionStackFull("意外：栈溢出");
        }
        elementData[++top] = e;
    }

    /**
     * 出栈
     *
     * @return
     * @throws ExceptionStackEmpty
     */
    @Override
    public E pop() throws ExceptionStackEmpty {
        E element;
        assertEmpty();
        element = elementData(top);
        elementData[top--] = null;
        return element;
    }

    /**
     * 获取长度
     *
     * @return
     */
    @Override
    public int size() {
        return (top + 1);
    }

    /**
     * 是否为空
     *
     * @return
     */
    @Override
    public boolean isEmpty() {
        return top < 0;
    }

    /**
     * 获取最顶端元素
     *
     * @return
     * @throws ExceptionStackEmpty
     */
    @Override
    public E top() throws ExceptionStackEmpty {
        assertEmpty();
        return elementData(top);
    }

    protected void assertEmpty() {
        if (isEmpty()) {
            throw new ExceptionStackEmpty("意外：栈空");
        }
    }

    E elementData(int index) {
        return (E) elementData[index];
    }
}
