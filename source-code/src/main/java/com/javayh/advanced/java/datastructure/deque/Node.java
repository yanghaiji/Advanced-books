package com.javayh.advanced.java.datastructure.deque;

import com.javayh.advanced.java.datastructure.Position;

/**
 * <p>
 * 基于位置接口实现的双向链表节点类
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-21
 */
public class Node<E> implements Position<E> {
    //数据对象
    private E element;
    //指向前驱节点
    private Node<E> prev;
    //指向后继节点
    private Node<E> next;

    public Node() {
        this(null,null,null);
    }

    /**
     *  注意三个参数的次序：数据对象、前驱节点、后继节点
     * @param e 数据对象
     * @param p 前驱节点
     * @param n 后继节点
     */
    public Node(E e, Node<E> p, Node<E> n)
    {
        element = e;
        prev = p;
        next = n;
    }


    @Override
    public E getElem() {
        return element;
    }

    /**
     * 将给定的元素放入该位置，并返回原来的元素
     * @param e 插入元素
     * @return
     */
    @Override
    public E setElem(E e) {
        E oldElem = element;
        element = e;
        return oldElem;
    }

    /**
     * 找到后继位置
     * @return
     */
    public Node<E> getNext()
    {
        return next;
    }

    /**
     * 找到前驱位置
     * @return
     */
    public Node<E> getPrev()
    {
        return prev;
    }

    /**
     * 修改后继位置
     * @param newNext
     */
    public void setNext(Node<E> newNext)
    {
        next = newNext;
    }

    /**
     * 修改前驱位置
     * @param newPrev
     */
    public void setPrev(Node<E> newPrev)
    {
        prev = newPrev;
    }
}
