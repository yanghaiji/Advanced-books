package com.javayh.advanced.java.datastructure;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-08-21
 */
public interface Position<E> {

    /**
     * 获取元素
     *
     * @return
     */
    E getElem();

    /**
     * 将给定元素存放至该位置，返回此前存放的元素
     *
     * @param e 插入元素
     * @return
     */
    E setElem(E e);
}
