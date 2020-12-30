package com.javayh.advanced.datastructure.vector;

import com.javayh.advanced.exception.ExceptionBoundaryViolation;

/**
 * <p>
 * 动态扩容
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-22
 */
public class ExtArrayVector<E> implements Vector<E> {

    private int DEFAULT_CAPACITY = 16;
    //向量的实际规模
    private int size;
    //对象数组
    private Object elementData[];

    public ExtArrayVector() {
        this.elementData = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public ExtArrayVector(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new ExceptionBoundaryViolation("initialCapacity :" + initialCapacity);
        }
        this.elementData = new Object[initialCapacity];
        this.size = 0;
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
        if (0 > r || r > size) {
            throw new ExceptionBoundaryViolation("下标越界");
        }
        return elementData(r);
    }

    @Override
    public E replaceAtRank(int r, E obj) throws ExceptionBoundaryViolation {
        if (r > size) {
            throw new ExceptionBoundaryViolation("下标越界");
        }
        E oldEle = elementData(r);
        elementData[r] = obj;
        return oldEle;
    }

    @Override
    public E insertAtRank(int r, E obj) throws ExceptionBoundaryViolation {
        if (0 > r) {
            throw new ExceptionBoundaryViolation("下标越界");
        }
        //空间溢出的处理
        if (DEFAULT_CAPACITY <= size) {
            DEFAULT_CAPACITY *= 2;
            //开辟一个容量加倍的数组
            Object[] elementCopy = new Object[DEFAULT_CAPACITY];
            for (int i = 0; i < size; i++) {
                //elementData[]中内容复制至elementCopy[]
                elementCopy[i] = elementData[i];
            }
            //用elementCopy替换elementData（原elementData[]将被自动回收）
            elementData = elementCopy;
        }
        //后续元素顺次后移
        for (int i = size; i > r; i--) {
            elementData[i] = elementData[i - 1];
        }
        elementData[r] = obj;//插入
        size++;//更新当前规模
        return obj;
    }

    @Override
    public E removeAtRank(int r) throws ExceptionBoundaryViolation {
        if (0 > r || r > size) {
            throw new ExceptionBoundaryViolation("下标越界");
        }
        E oldEle = elementData(r);
        System.arraycopy(elementData, r + 1, elementData, r, size - r);
        size--;//更新当前规模
        return oldEle;
    }

    E elementData(int index) {
        return (E) elementData[index];
    }
}
