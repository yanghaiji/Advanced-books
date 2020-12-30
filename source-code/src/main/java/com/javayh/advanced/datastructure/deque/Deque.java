package com.javayh.advanced.datastructure.deque;

import com.javayh.advanced.exception.ExceptionQueueEmpty;

/**
 * <p>
 * 自定义双端队列接口
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-21
 */
public interface Deque<E> {
    /**
     * 返回队列中元素数目
     *
     * @return
     */
    int size();

    /**
     * 判断队列是否为空
     *
     * @return
     */
    boolean isEmpty();

    /**
     * 取首元素（但不删除）
     *
     * @return
     * @throws ExceptionQueueEmpty
     */
    E first() throws ExceptionQueueEmpty;

    /**
     * 取末元素（但不删除）
     *
     * @return
     * @throws ExceptionQueueEmpty
     */
    E last() throws ExceptionQueueEmpty;

    /**
     * 将新元素作为首元素插入
     *
     * @param obj
     */
    void addFirst(E obj);

    /**
     * 将新元素作为末元素插入
     *
     * @param obj
     */
    void aadLast(E obj);

    /**
     * 删除首元素
     *
     * @return
     * @throws ExceptionQueueEmpty
     */
    E removeFirst() throws ExceptionQueueEmpty;

    /**
     * 删除末元素
     *
     * @return
     * @throws ExceptionQueueEmpty
     */
    E removeLast() throws ExceptionQueueEmpty;

    /**
     * 遍历
     */
    void iterator();
}
