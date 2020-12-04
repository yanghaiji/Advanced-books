package com.javayh.advanced.java.datastructure.linked;

import com.javayh.advanced.java.datastructure.Position;

/**
 * <p>
 * 单链表节点
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-21
 */
public class Node<E> implements Position<E> {
    //数据对象
    private E element;
    //指向后继节点
    private Node<E> next;

    /**
     * 指向数据对象、后继节点的引用都置空
     */
    public Node() {
        this(null, null);
    }

    /**
     * 指定数据对象及后继节点
     *
     * @param e
     * @param n
     */
    public Node(E e, Node<E> n) {
        element = e;
        next = n;
    }


    @Override
    public E getElem() {
        return element;
    }

    @Override
    public E setElem(E e) {
        E oldElem = element;
        element = e;
        return oldElem;
    }

    /**
     * 取当前节点的后继节点
     *
     * @return
     */
    public Node<E> getNext() {
        return next;
    }

    /**
     * 修改当前节点的后继节点
     *
     * @param newNext
     */
    public void setNext(Node<E> newNext) {
        next = newNext;
    }

}
