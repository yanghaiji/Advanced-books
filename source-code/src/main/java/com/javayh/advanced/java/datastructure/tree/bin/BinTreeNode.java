package com.javayh.advanced.java.datastructure.tree.bin;

import com.javayh.advanced.java.datastructure.Iterator;
import com.javayh.advanced.java.datastructure.list.List;
import com.javayh.advanced.java.datastructure.list.NodeList;
import com.javayh.advanced.java.datastructure.queue.QueueArray;
import lombok.SneakyThrows;

import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-08
 */
public class BinTreeNode<E> implements BinTreePosition<E>{

    protected E element;//该节点中存放的对象
    protected BinTreePosition<E> parent;//父亲
    protected BinTreePosition<E> lChild;//左孩子
    protected BinTreePosition<E> rChild;//右孩子
    protected int size;//后代数目
    protected int height;//高度
    protected int depth;//深度
    public BinTreeNode()
    {
        this(null, null, true, null, null);
    }

    public BinTreeNode(
            E e,//节点内容
            BinTreePosition<E> p,//父节点
            boolean asLChild,//是否作为父节点的左孩子
            BinTreePosition<E> l,//左孩子
            BinTreePosition<E> r)//右孩子
    {
        size = 1; height = depth = 0; parent = lChild = rChild = null;//初始化
        element = e;//存放的对象
        //建立与父亲的关系
        if (null != p){
            if (asLChild){
                p.attachL(this);
            }
        }else {p.attachR(this);}
        //建立与孩子的关系
        if (null != l) {
            attachL(l);
        }
        if (null != r) {
            attachR(r);
        }
    }
    @Override
    public boolean hasParent() {
        return null != parent;
    }

    @Override
    public BinTreePosition<E> getParent() {
        return parent;
    }

    @Override
    public void setParent(BinTreePosition<E> p) {
        this.parent=p;
    }

    @Override
    public boolean isLeaf() {
        return !hasLChild() && !hasRChild();
    }

    /**
     * <p>
     *       若当前节点有父亲，而且是左孩子，则返回true；否则，返回false
     * </p>
     * @param
     * @return boolean
     */
    @Override
    public boolean isLChild() {
        return hasParent() && this == getParent().getLChild();
    }

    @Override
    public boolean hasLChild() {
        return  null != lChild;
    }

    @Override
    public BinTreePosition<E> getLChild() {
        return this.lChild;
    }

    @Override
    public void setLChild(BinTreePosition<E> c) {
        this.lChild = c;
    }

    /**
     * <p>
     *      判断是否为右孩子（为使代码描述简洁）
     *      若当前节点有父亲，而且是右孩子，则返回true；否则，返回false
     * </p>
     * @param
     * @return boolean
     */
    @Override
    public boolean isRChild() {
        return hasParent() && this == getParent().getRChild();
    }

    @Override
    public boolean hasRChild() {
        return null != this.rChild;
    }

    @Override
    public BinTreePosition<E> getRChild() {
        return this.rChild;
    }

    @Override
    public void setRChild(BinTreePosition<E> c) {
        this.rChild = c;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void updateSize() {
        //当前节点
        size = 1;
        if(hasLChild()){
            size += getLChild().getSize();
        }
        if (hasRChild()){
            size += getRChild().getSize();
        }
        if (hasParent()){//递归更新各个真祖先的规模记录
            getParent().updateSize();
        }
    }

    @Override
    public int getHeight() {
        return height;
    }

    /**
     * 在孩子发生变化后，更新当前节点及其祖先的高度
     */
    @Override
    public void updateHeight() {
        //先假设没有左、右孩子
        height = 0;
        if(hasLChild()){
            this.height = Math.max(height,1+getLChild().getHeight());
        }//
        if(hasRChild()){
            height = Math.max(height, 1+getRChild().getHeight());
        }
        if (hasParent()) {//递归更新各个真祖先的高度记录
            getParent().updateHeight();
        }
    }

    @Override
    public int getDepth() {
        return this.depth;
    }

    /**
     * 在父亲发生变化后，更新当前节点及其后代的深度
     */
    @Override
    public void updateDepth() {
        depth = hasParent() ? 1+getParent().getDepth() : 0;//当前节点
        if (hasLChild()) {
            getLChild().updateDepth();//沿孩子引用逐层向下，
        }
        if (hasRChild()) {
            getRChild().updateDepth();//递归地更新所有后代的深度记录
        }
    }

    /**
     * /按照中序遍历的次序，找到当前节点的直接前驱
     * @return
     */
    @Override
    public BinTreePosition<E> getPrev() {
        //若左子树非空，则其中的最大者即为当前节点的直接前驱
        if (hasLChild()) {
            return findMaxDescendant(getLChild());
        }
        //至此，当前节点没有左孩子
        if (isRChild()) {
            return getParent();//若当前节点是右孩子，则父亲即为其直接前驱
        }
        //至此，当前节点没有左孩子，而且是左孩子
        BinTreePosition v = this;//从当前节点出发
        while (v.isLChild()){
            v = v.getParent();//沿左孩子链一直上升
        }
        //至此，v或者没有父亲，或者是父亲的右孩子
        return v.getParent();
    }

    /**
     * 按照中序遍历的次序，找到当前节点的直接后继
     * @return
     */
    @Override
    public BinTreePosition<E> getSucc() {
        //若右子树非空，则其中的最小者即为当前节点的直接后继
        if (hasRChild()) {
            return findMinDescendant(getRChild());
        }
        //至此，当前节点没有右孩子
        if (isLChild()) {
            return getParent();//若当前节点是左孩子，则父亲即为其直接后继
        }
        //至此，当前节点没有右孩子，而且是右孩子
        BinTreePosition v = this;//从当前节点出发
        while (v.isRChild()) {
            v = v.getParent();//沿右孩子链一直上升
        }
        //至此，v或者没有父亲，或者是父亲的左孩子
        return v.getParent();
    }

    @Override
    public BinTreePosition<E> secede() {
        if (null != parent) {
            if (isLChild()) {
                parent.setLChild(null);//切断父亲指向当前节点的引用
            }
            else {
                parent.setRChild(null);
            }
            parent.updateSize();//更新当前节点及其祖先的规模
            parent.updateHeight();//更新当前节点及其祖先的高度
            parent = null;//切断当前节点指向原父亲的引用
            updateDepth();//更新节点及其后代节点的深度
        }
        return this;//返回当前节点
    }

    /**
     * 将节点c作为当前节点的左孩子
     * @param c
     * @return
     */
    @Override
    public BinTreePosition<E> attachL(BinTreePosition<E> c) {
        if (hasLChild()) {
            getLChild().secede();//摘除当前节点原先的左孩子
        }
        if (null != c) {
            c.secede();//c脱离原父亲
            lChild = c; c.setParent(this);//确立新的父子关系
            updateSize();//更新当前节点及其祖先的规模
            updateHeight();//更新当前节点及其祖先的高度
            c.updateDepth();//更新c及其后代节点的深度
        }
        return this;
    }

    /**
     * 将节点c作为当前节点的右孩子
     * @param c
     * @return
     */
    @Override
    public BinTreePosition<E> attachR(BinTreePosition<E> c) {
        if (hasRChild()) {
            getRChild().secede();//摘除当前节点原先的右孩子
        }
        if (null != c) {
            c.secede();//c脱离原父亲
            rChild = c; c.setParent(this);//确立新的父子关系
            updateSize();//更新当前节点及其祖先的规模
            updateHeight();//更新当前节点及其祖先的高度
            c.updateDepth();//更新c及其后代节点的深度
        }
        return this;
    }

    @Override
    public Iterator elementsPreorder() {
        List list = new NodeList();
        preorder(list, this);
        return list.elements();
    }

    @Override
    public Iterator elementsInorder() {
        List list = new NodeList();
        inorder(list, this);
        return list.elements();
    }

    @Override
    public Iterator elementsPostorder() {
        List list = new NodeList();
        postorder(list, this);
        return list.elements();
    }

    @Override
    public Iterator elementsLevelorder() {
        List list = new NodeList();
        levelorder(list, this);
        return list.elements();
    }

    @Override
    public E getElem() {
        return element;
    }

    @Override
    public E setElem(E e) {
        E old = this.element;
        this.element = e;
        return old;
    }

    //在v的后代中，找出最小者
    protected static BinTreePosition findMinDescendant(BinTreePosition v) {
        if (null != v)
            while (v.hasLChild()) v = v.getLChild();//从v出发，沿左孩子链一直下降
        //至此，v或者为空，或者没有左孩子
        return v;
    }
    //在v的后代中，找出最大者
    protected static BinTreePosition findMaxDescendant(BinTreePosition v) {
        if (null != v)
            while (v.hasRChild()) v = v.getRChild();//从v出发，沿右孩子链一直下降
        //至此，v或者为空，或者没有右孩子
        return v;
    }
    //前序遍历以v为根节的（子）树
    protected static void preorder(List list, BinTreePosition v) {
        if (null == v) return;//递归基：空树
        list.insertLast(v);//访问v
        preorder(list, v.getLChild());//遍历左子树
        preorder(list, v.getRChild());//遍历右子树
    }
    //中序遍历以v为根节的（子）树
    protected static void inorder(List list, BinTreePosition v) {
        if (null == v) return;//递归基：空树
        inorder(list, v.getLChild());//遍历左子树
        list.insertLast(v);//访问v
        inorder(list, v.getRChild());//遍历右子树
    }
    //后序遍历以v为根节的（子）树
    protected static void postorder(List list, BinTreePosition v) {
        if (null == v) return;//递归基：空树
        postorder(list, v.getLChild());//遍历左子树
        postorder(list, v.getRChild());//遍历右子树
        list.insertLast(v);//访问v
    }
    //层次遍历以v为根节的（子）树
    @SneakyThrows
    protected static void levelorder(List list, BinTreePosition v) {
        QueueArray Q = new QueueArray();//空队
        Q.enqueue(v);//根节点入队
        while (!Q.isEmpty()) {
            BinTreePosition u = (BinTreePosition) Q.dequeue();//出队
            list.insertLast(u);//访问v
            if (u.hasLChild()) {
                Q.enqueue(u.getLChild());
            }
            if (u.hasRChild()) {
                Q.enqueue(u.getRChild());
            }
        }
    }
}
