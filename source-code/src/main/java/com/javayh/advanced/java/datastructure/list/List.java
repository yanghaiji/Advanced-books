package com.javayh.advanced.java.datastructure.list;

import com.javayh.advanced.exception.ExceptionBoundaryViolation;
import com.javayh.advanced.exception.ExceptionPositionInvalid;
import com.javayh.advanced.java.datastructure.Position;

/**
 * <p>
 *      自定义列表
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-25
 */
public interface List<E> {
    //查询列表当前的规模
    int size();
    //判断列表是否为空
    boolean isEmpty();
    //返回第一个元素（的位置）
    Position<E> first();
    //返回最后一个元素（的位置）
    Position<E> last();
    //返回紧接给定位置之后的元素（的位置）
    Position<E> getNext(Position<E> p) throws ExceptionPositionInvalid, ExceptionBoundaryViolation;
    //返回紧靠给定位置之前的元素（的位置）
    Position<E> getPrev(Position<E> p) throws ExceptionPositionInvalid, ExceptionBoundaryViolation;
    //将e作为第一个元素插入列表
    Position<E> insertFirst(E e);
    //将e作为最后一个元素插入列表
    Position<E> insertLast(E e);
    //将e插入至紧接给定位置之后的位置
    Position<E> insertAfter(Position<E> p, E e) throws ExceptionPositionInvalid;
    //将e插入至紧靠给定位置之前的位置
    Position<E> insertBefore(Position<E> p, E e) throws ExceptionPositionInvalid;
    //删除给定位置处的元素，并返回之
    E remove(Position<E> p) throws ExceptionPositionInvalid;
    //删除首元素，并返回之
    E removeFirst();
    //删除末元素，并返回之
    E removeLast();
    //将处于给定位置的元素替换为新元素，并返回被替换的元素
    E replace(Position<E> p, E e)  throws ExceptionPositionInvalid;
}
