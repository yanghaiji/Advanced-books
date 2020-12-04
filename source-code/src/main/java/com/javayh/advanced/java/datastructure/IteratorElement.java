package com.javayh.advanced.java.datastructure;

import com.javayh.advanced.exception.ExceptionNoSuchElement;
import com.javayh.advanced.java.datastructure.list.List;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-09
 */
public class IteratorElement<E> implements Iterator<E> {

    private List list;//列表
    private Position nextPosition;//当前（下一个）元素的位置

    //默认构造方法
    public IteratorElement() {
        list = null;
    }

    //构造方法
    public IteratorElement(List L) {
        list = L;
        if (list.isEmpty())//若列表为空，则
            nextPosition = null;//当前元素置空
        else//否则
            nextPosition = list.first();//从第一个元素开始
    }


    @Override
    public E next() throws ExceptionNoSuchElement {
        if (!hasNext()) throw new ExceptionNoSuchElement("意外：没有下一元素");
        Position currentPosition = nextPosition;
        if (currentPosition == list.last())//若已到达尾元素，则
            nextPosition = null;//不再有下一元素
        else//否则
            nextPosition = list.getNext(currentPosition);//转向下一元素
        return (E) currentPosition.getElem();
    }

    @Override
    public boolean hasNext() {
        return (null != nextPosition);
    }
}
