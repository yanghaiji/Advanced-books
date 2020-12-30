package com.javayh.advanced.datastructure.tree.bin;

import com.javayh.advanced.datastructure.Iterator;

/**
 * <p>
 * 二叉树
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-07
 */
public interface BinTree<E> {

    /**
     * <p>
     * 获取根节点
     * </p>
     *
     * @param
     * @return com.javayh.advanced.datastructure.tree.bin.BinTreePosition
     */
    BinTreePosition getRoot();

    /**
     * <p>
     * 判断是否为空
     * </p>
     *
     * @param
     * @return boolean
     */
    boolean isEmpty();

    /**
     * 返回树的规模，即树根的后代数目
     *
     * @return int
     */
    int size();

    /**
     * <p>
     * 返回树的高度
     * </p>
     *
     * @param
     * @return int
     */
    int getHeight();

    /**
     * <p>
     * 前序遍历
     * </p>
     *
     * @param
     * @return com.javayh.advanced.datastructure.Iterator<E>
     */
    Iterator<E> elementsPreorder();

    /**
     * <p>
     * 中序遍历
     * </p>
     *
     * @param
     * @return com.javayh.advanced.datastructure.Iterator
     */
    Iterator<E> elementsInorder();

    /**
     * <p>
     * 后序遍历
     * </p>
     *
     * @param
     * @return com.javayh.advanced.datastructure.Iterator
     */
    Iterator<E> elementsPostorder();

    /**
     * <p>
     * 层次遍历
     * </p>
     *
     * @param
     * @return com.javayh.advanced.datastructure.Iterator
     */
    Iterator<E> elementsLevelorder();
}
