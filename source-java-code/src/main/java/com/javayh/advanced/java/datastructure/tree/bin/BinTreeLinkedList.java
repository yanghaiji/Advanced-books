package com.javayh.advanced.java.datastructure.tree.bin;

import com.javayh.advanced.java.datastructure.Iterator;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-09
 */
public class BinTreeLinkedList implements BinTree {
    protected BinTreePosition root;//根节点

    public BinTreeLinkedList() {
        this(null);
    }

    public BinTreeLinkedList(BinTreePosition r) {
        root = r;
    }

    //返回树根
    @Override
    public BinTreePosition getRoot() {
        return root;
    }

    //判断是否树空
    @Override
    public boolean isEmpty() {
        return null == root;
    }

    //返回树的规模（即树根的后代数目）
    @Override
    public int size() {
        return isEmpty() ? 0 : root.getSize();
    }

    //返回树（根）的高度
    @Override
    public int getHeight() {
        return isEmpty() ? -1 : root.getHeight();
    }

    //前序遍历
    @Override
    public Iterator elementsPreorder() {
        return root.elementsPreorder();
    }

    //中序遍历
    @Override
    public Iterator elementsInorder() {
        return root.elementsInorder();
    }

    //后序遍历
    @Override
    public Iterator elementsPostorder() {
        return root.elementsPostorder();
    }

    //层次遍历
    @Override
    public Iterator elementsLevelorder() {
        return root.elementsLevelorder();
    }
}
