package com.javayh.advanced.java.datastructure.list;

import com.javayh.advanced.exception.ExceptionBoundaryViolation;
import com.javayh.advanced.exception.ExceptionListEmpty;
import com.javayh.advanced.exception.ExceptionPositionInvalid;
import com.javayh.advanced.java.datastructure.Position;
import com.javayh.advanced.java.datastructure.deque.Node;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-26
 */
public class NodeList<E> implements List<E>{
    //列表的实际规模
    protected int numElem;
    //哨兵：首节点+末节点
    protected Node<E> header, trailer;

    public NodeList() {
        //空表
        numElem = 0;
        //首节点
        header = new Node<E>(null, null, null);
        //末节点
        trailer = new Node<E>(null, header, null);
        //首、末节点相互链接
        header.setNext(trailer);
    }

    @Override
    public int size() {
        return numElem;
    }

    @Override
    public boolean isEmpty() {
        return 0 == numElem;
    }

    @Override
    public Position<E> first() {
        isNull();
        return header.getNext();
    }

    @Override
    public Position<E> last() {
        isNull();
        return trailer.getPrev();
    }

    @Override
    public Position<E> getNext(Position<E> p) throws ExceptionPositionInvalid, ExceptionBoundaryViolation {
        Node<E> v = checkPosition(p);
        Node<E> next = v.getNext();
        if (next == trailer){
            throw new ExceptionBoundaryViolation("意外：企图越过列表后端");
        }
        return next;
    }

    @Override
    public Position<E> getPrev(Position<E> p) throws ExceptionPositionInvalid, ExceptionBoundaryViolation {
        Node<E> v = checkPosition(p);
        Node<E> prev = v.getPrev();
        if (prev == header) {
            throw new ExceptionBoundaryViolation("意外：企图越过列表前端");
        }
        return prev;
    }

    @Override
    public Position<E> insertFirst(E e) {
        numElem++;
        Node newNode = new Node(e, header, header.getNext());
        header.getNext().setPrev(newNode);
        header.setNext(newNode);
        return newNode;
    }

    @Override
    public Position<E> insertLast(E e) {
        numElem++;
        Node newNode = new Node(e, trailer.getPrev(), trailer);
        if (null == trailer.getPrev()) {
            System.out.println("Prev of trailer is Null");
        }
        trailer.getPrev().setNext(newNode);
        trailer.setPrev(newNode);
        return newNode;
    }

    @Override
    public Position<E> insertAfter(Position<E> p, E element) throws ExceptionPositionInvalid {
        Node v = checkPosition(p);
        numElem++;
        Node newNode = new Node(element, v, v.getNext());
        v.getNext().setPrev(newNode);
        v.setNext(newNode);
        return newNode;
    }

    @Override
    public Position<E> insertBefore(Position<E> p, E element) throws ExceptionPositionInvalid {
        Node v = checkPosition(p);
        numElem++;
        Node newNode = new Node(element, v.getPrev(), v);
        v.getPrev().setNext(newNode);
        v.setPrev(newNode);
        return newNode;
    }

    @Override
    public E remove(Position<E> p) throws ExceptionPositionInvalid {
        Node v = checkPosition(p);
        numElem--;
        Node vPrev = v.getPrev();
        Node vNext = v.getNext();
        vPrev.setNext(vNext);
        vNext.setPrev(vPrev);
        Object vElem = v.getElem();
        //将该位置（节点）从列表中摘出，以便系统回收其占用的空间
        v.setNext(null);
        v.setPrev(null);
        return (E) vElem;
    }

    @Override
    public E removeFirst() {
        return remove(header.getNext());
    }

    @Override
    public E removeLast() {
        return remove(trailer.getPrev());
    }

    @Override
    public E replace(Position<E> p, E element) throws ExceptionPositionInvalid {
        Node v = checkPosition(p);
        Object oldElem = v.getElem();
        v.setElem(element);
        return (E) oldElem;
    }

    /**
     * 检查给定位置在列表中是否合法，若是，则将其转换为 Node
     * @param p
     * @return
     * @throws ExceptionPositionInvalid
     */
    protected Node<E> checkPosition(Position<E> p) throws ExceptionPositionInvalid {
        if (null == p) {
            throw new ExceptionPositionInvalid("意外：传递给List_DLNode的位置是null");
        }
        if (header == p) {
            throw new ExceptionPositionInvalid("意外：头节点哨兵位置非法");
        }
        if (trailer == p) {
            throw new ExceptionPositionInvalid("意外：尾结点哨兵位置非法");
        }
        Node temp = (Node)p;
        return temp;
    }

    protected void isNull(){
        if (isEmpty()){
            throw new ExceptionListEmpty("意外：列表空");
        }
    }
}
