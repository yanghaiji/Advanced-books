package com.javayh.advanced.java.datastructure.deque;

import com.javayh.advanced.exception.ExceptionQueueEmpty;

/**
 * <p>
 * 自定义双端队列
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-21
 */
public class LinkedDeque<E> implements Deque<E> {

    //指向头节点（哨兵）
    protected Node<E> header;
    //指向尾节点（哨兵）
    protected Node<E> trailer;
    //队列中元素的数目
    protected int size;

    public LinkedDeque() {
        header = new Node<E>();
        trailer = new Node<E>();
        header.setNext(trailer);
        trailer.setPrev(header);
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public E first() throws ExceptionQueueEmpty {
        if (isEmpty()) {
            throw new ExceptionQueueEmpty("意外：双端队列为空");
        }
        return header.getNext().getElem();
    }

    @Override
    public E last() throws ExceptionQueueEmpty {
        if (isEmpty()) {
            throw new ExceptionQueueEmpty("意外：双端队列为空");
        }
        return trailer.getPrev().getElem();
    }

    @Override
    public void addFirst(E obj) {
        Node<E> second = header.getNext();
        Node<E> first = new Node<>(obj, header, second);
        second.setPrev(first);
        header.setNext(first);
        size++;
    }

    @Override
    public void aadLast(E obj) {
        Node<E> second = trailer.getPrev();
        Node<E> first = new Node<E>(obj, second, trailer);
        second.setNext(first);
        trailer.setPrev(first);
        size++;
    }

    @Override
    public E removeFirst() throws ExceptionQueueEmpty {
        if (isEmpty()) {
            throw new ExceptionQueueEmpty("意外：双端队列为空");
        }
        Node<E> first = header.getNext();
        Node<E> second = first.getNext();
        E obj = first.getElem();
        header.setNext(second);
        second.setPrev(header);
        size--;
        return obj;
    }

    @Override
    public E removeLast() throws ExceptionQueueEmpty {
        if (isEmpty()) {
            throw new ExceptionQueueEmpty("意外：双端队列为空");
        }
        Node<E> first = trailer.getPrev();
        Node<E> second = first.getPrev();
        E obj = first.getElem();
        trailer.setPrev(second);
        second.setNext(trailer);
        size--;
        return obj;
    }

    @Override
    public void iterator() {
        Node<E> p = header.getNext();
        while (p != trailer) {
            System.out.print(p.getElem() + " ");
            p = p.getNext();
        }
        System.out.println();
    }
}
