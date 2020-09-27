package com.javayh.advanced.java.datastructure.tree.bin;

import com.javayh.advanced.java.datastructure.Iterator;
import com.javayh.advanced.java.datastructure.Position;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-07
 */
public interface BinTreePosition<E> extends Position<E> {

    //判断是否有父亲
    boolean hasParent();
    //返回当前节点的父节点
    BinTreePosition<E> getParent();
    //设置当前节点的父节点
    void setParent(BinTreePosition<E> p);
    //判断是否为叶子
    boolean isLeaf();
    //判断是否为左孩子（为使代码描述简洁）
    boolean isLChild();
    //判断是否有左孩子（为使代码描述简洁）
    boolean hasLChild();
    //返回当前节点的左孩子
    BinTreePosition<E> getLChild();
    //设置当前节点的左孩子（注意：this.lChild和c.parent都不一定为空）
    void setLChild(BinTreePosition<E> c);
    //判断是否为右孩子（为使代码描述简洁）
    boolean isRChild();
    //判断是否有右孩子（为使代码描述简洁）
    boolean hasRChild();
    //返回当前节点的右孩子
    BinTreePosition<E> getRChild();
    //设置当前节点的右孩子（注意：this.rChild和c.parent都不一定为空）
    void setRChild(BinTreePosition<E> c);
    //返回当前节点后代元素的数目
    int getSize();
    //在孩子发生变化后，更新当前节点及其祖先的规模
    void updateSize();
    //返回当前节点的高度
    int getHeight();
    //在孩子发生变化后，更新当前节点及其祖先的高度
    void updateHeight();
    //返回当前节点的深度
    int getDepth();
    //在父亲发生变化后，更新当前节点及其后代的深度
    void updateDepth();
    //按照中序遍历的次序，找到当前节点的直接前驱
    BinTreePosition<E> getPrev();
    //按照中序遍历的次序，找到当前节点的直接后继
    BinTreePosition<E> getSucc();
    //断绝当前节点与其父亲的父子关系
    //返回当前节点
    BinTreePosition<E> secede();
    //将节点c作为当前节点的左孩子
    BinTreePosition<E> attachL(BinTreePosition<E> c);
    //将节点c作为当前节点的右孩子
    BinTreePosition<E> attachR(BinTreePosition<E> c);
    //前序遍历
    Iterator elementsPreorder();
    //中序遍历
    Iterator elementsInorder();
    //后序遍历
    Iterator elementsPostorder();
    //层次遍历
    Iterator elementsLevelorder();
}
