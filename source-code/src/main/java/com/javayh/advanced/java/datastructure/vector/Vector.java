package com.javayh.advanced.java.datastructure.vector;

import com.javayh.advanced.exception.ExceptionBoundaryViolation;

/**
 * <p>
 * 向量
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-22
 */
public interface Vector<E> {

    /**
     * 返回向量中元素数目
     * @return
     */
    int size();

    /**
     * 判断向量是否为空
     * @return
     */
    boolean isEmpty();

    /**
     * 取秩为r的元素
     * @param r
     * @return
     * @throws ExceptionBoundaryViolation
     */
    E getAtRank(int r) throws ExceptionBoundaryViolation;

    /**
     * 替换指定下标的元素
     * @param r
     * @param obj
     * @return
     * @throws ExceptionBoundaryViolation
     */
    E replaceAtRank(int r, E obj) throws ExceptionBoundaryViolation;

    /**
     * 插入obj，作为秩为r的元素；返回该元素
     * @param r
     * @param obj
     * @return
     */
    E insertAtRank(int r, E obj) throws ExceptionBoundaryViolation;

    /**
     * 删除秩为r的元素
     * @param r
     * @return
     * @throws ExceptionBoundaryViolation
     */
    E removeAtRank(int r) throws ExceptionBoundaryViolation;
}
