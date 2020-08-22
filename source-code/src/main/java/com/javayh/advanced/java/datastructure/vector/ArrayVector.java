package com.javayh.advanced.java.datastructure.vector;

import com.javayh.advanced.exception.ExceptionBoundaryViolation;

/**
 * <p>
 *      基于数组实现的向量
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-22
 */
public class ArrayVector<E> implements Vector<E>{
    //数组的容量
    private final int DEFAULT_CAPACITY = 1024;
    //向量的实际规模
    private int size = 0;
    //对象数组
    private final Object[] elementData;

    public ArrayVector() {
        this.elementData = new Object[DEFAULT_CAPACITY];
        this.size=0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return 0 == size;
    }

    @Override
    public E getAtRank(int r) throws ExceptionBoundaryViolation {
        if (0 > r || r >= size) {
            throw new ExceptionBoundaryViolation("意外：秩越界");
        }
        return elementData(r);
    }

    @Override
    public E replaceAtRank(int r, E obj) throws ExceptionBoundaryViolation {
        if (0 > r || r >= size) {
            throw new ExceptionBoundaryViolation("意外：秩越界");
        }
        E oldEle = elementData(r);
        elementData[r] = obj;
        return oldEle;
    }

    @Override
    public E insertAtRank(int r, E obj) throws ExceptionBoundaryViolation {
        if (0 > r || r >= DEFAULT_CAPACITY) {
            throw new ExceptionBoundaryViolation("意外：秩越界");
        }
        if (size >= DEFAULT_CAPACITY){
            throw new ExceptionBoundaryViolation("意外：数组溢出");
        }
        /*for (int i= size; i > r; i--) {
            //后续元素顺次后移
            elementData[i] = elementData[i-1];
        }//插入
        elementData[r] = obj;*/
        //后续元素顺次后移
        System.arraycopy(elementData, r, elementData, r + 1, size - r);
        //插入
        elementData[r] = obj;
        //更新当前规模
        size++;
        return obj;
    }

    @Override
    public E removeAtRank(int r) throws ExceptionBoundaryViolation {
        if (0 > r || r >= size) {
            throw new ExceptionBoundaryViolation("意外：秩越界");
        }
        E bak = elementData(r);
        //后续元素顺次前移
        /*for (int i=r; i<size; i++){
            //后续元素顺次前移
            elementData[i] = elementData[i+1];
        }*/
        System.arraycopy(elementData, r + 1, elementData, r, size - r);
        size--;//更新当前规模
        return bak;
    }

    E elementData(int index) {
        return (E) elementData[index];
    }

}
