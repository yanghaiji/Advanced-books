package com.javayh.advanced.datastructure.stack;

import com.javayh.advanced.exception.ExceptionStackEmpty;
import com.javayh.advanced.datastructure.linked.Node;

/**
 * <p>
 * 单链表实现栈
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-21
 */
public class LinkedStack<E> implements Stack<E> {

    //指向栈顶元素
    private Node top;
    //栈中元素的数目
    private int size;

    public LinkedStack() {
        this.size = 0;
        this.top = null;
    }

    @Override
    public void push(E e) {
        //创建一个新节点，将其作为首节点插入
        //Node<E> node = new Node<>(e,top);
        //更新首节点引用
        //top = node;
        top = new Node<>(e, top);
        //更新操作长度
        size++;
    }

    @Override
    public E pop() throws ExceptionStackEmpty {
        if (isEmpty()) {
            throw new ExceptionStackEmpty("意外：栈空");
        }
        //获取当前的节点
        E elem = (E) top.getElem();
        //重置首节点元素
        top = top.getNext();
        //操作长度减一
        size--;
        return elem;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return top == null;
    }

    @Override
    public E top() throws ExceptionStackEmpty {
        if (isEmpty()) {
            throw new ExceptionStackEmpty("意外：栈空");
        }
        return (E) top.getElem();
    }
}
