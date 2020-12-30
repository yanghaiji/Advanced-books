package com.javayh.advanced.datastructure.stack;

import com.javayh.advanced.exception.ExceptionStackEmpty;

/**
 * <p>
 * 自定义栈
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-19
 */
public interface Stack<E> {

    /**
     * 入栈
     *
     * @param e
     */
    void push(E e);

    /**
     * 出栈
     */
    E pop() throws ExceptionStackEmpty;

    /**
     * 获取长度
     *
     * @return
     */
    int size();

    /**
     * 判断是否为空
     *
     * @return
     */
    boolean isEmpty();

    /**
     * 取栈顶元素（但不删除）
     *
     * @return
     * @throws ExceptionStackEmpty
     */
    E top() throws ExceptionStackEmpty;
}
