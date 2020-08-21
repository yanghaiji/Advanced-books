package com.javayh.advanced.java.datastructure.queue;

import com.javayh.advanced.exception.ExceptionQueueEmpty;
import com.javayh.advanced.exception.ExceptionQueueFull;
import com.javayh.advanced.java.datastructure.linked.Node;

/**
 * <p>
 * 基于单链表实现的队列
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-21
 */
public class LinkedQueue<E> implements Queue<E>{
    //指向表首元素
    protected Node<E> head;
    //指向表末元素
    protected Node<E> tail;
    //队列中元素的数目
    protected int size;

    public LinkedQueue() {
        //构造方法（空队列）
        head = tail = null;
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
//        return (head = tail) == null;
        return 0 == size;
    }

    @Override
    public E front() throws ExceptionQueueEmpty {
        return head.getElem();
    }

    @Override
    public void enqueue(E e) throws ExceptionQueueFull {
        Node<E> node = new Node<>();
        node.setElem(e);
        node.setNext(null);
        if (0 == size){
            //若此前队列为空，则直接插入
            head = node;
        }else {
            tail.setNext(node);
        }
        //更新指向末节点引用
        tail = node;
        //更新规模
        size++;
    }

    @Override
    public E dequeue() throws ExceptionQueueEmpty {
        if(isEmpty()){
            throw new ExceptionQueueEmpty("意外：队列空");
        }
        E old = head.getElem();
        head = head.getNext();
        size--;
        //若队列已空，须将末节点引用置空
        if (0 == size){
            tail = null;
        }
        return old;
    }
}
