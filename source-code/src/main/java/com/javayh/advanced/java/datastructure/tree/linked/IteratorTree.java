package com.javayh.advanced.java.datastructure.tree.linked;

import com.javayh.advanced.exception.ExceptionNoSuchElement;
import com.javayh.advanced.java.datastructure.Iterator;
import com.javayh.advanced.java.datastructure.Position;
import com.javayh.advanced.java.datastructure.list.List;
import com.javayh.advanced.java.datastructure.queue.QueueArray;
import lombok.SneakyThrows;


/**
 * <p>
 * 树的迭代器
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-07
 */
public class IteratorTree<E> implements Iterator<E> {
    private List list;//列表
    private Position nextPosition;//当前（下一个）元素的位置
    //默认构造方法
    public IteratorTree() { list = null; }
    //前序遍历
    public void elementsPreorderIterator(TreeLinkedList T) {
        if (null == T) {return;}//递归基
        list.insertLast(T);//首先输出当前节点
        TreeLinkedList subtree = T.getFirstChild();//从当前节点的长子开始
        while (null != subtree) {//依次对当前节点的各个孩子
            this.elementsPreorderIterator(subtree);//做前序遍历
            subtree = subtree.getNextSibling();
        }
    }
    //后序遍历
    public void elementsPostorderIterator(TreeLinkedList T) {
        if (null == T) return;//递归基
        TreeLinkedList subtree = T.getFirstChild();//从当前节点的长子开始
        while (null != subtree) {//依次对当前节点的各个孩子
            this.elementsPostorderIterator(subtree);//做后序遍历
            subtree = subtree.getNextSibling();
        }
        list.insertLast(T);//当所有后代都访问过后，最后才访问当前节点
    }
    //层次遍历
    @SneakyThrows
    public void levelTraversalIterator(TreeLinkedList T) {
        if (null == T) return;
        QueueArray Q = new QueueArray();//空队
        Q.enqueue(T);//根节点入队
        while (!Q.isEmpty()) {//在队列重新变空之前
            TreeLinkedList tree = (TreeLinkedList) (Q.dequeue());//取出队列首节点
            list.insertLast(tree);//将新出队的节点接入迭代器中
            TreeLinkedList subtree = tree.getFirstChild();//从tree的第一个孩子起
            while (null != subtree) {//依次找出所有孩子，并
                Q.enqueue(subtree);//将其加至队列中
                subtree = subtree.getNextSibling();
            }
        }
    }

    //检查迭代器中是否还有剩余的元素
    @Override
    public boolean hasNext() {
        return (null != nextPosition);
    }

    //返回迭代器中的下一元素
    @SneakyThrows
    @Override
    public E next() {
        if (!hasNext()) {
            throw new ExceptionNoSuchElement("No next position");
        }
        Position currentPosition = nextPosition;
        if (currentPosition == list.last()) {//若已到达尾元素，则
            nextPosition = null;//不再有下一元素
        }
        else {//否则
            nextPosition = list.getNext(currentPosition);//转向下一元素
        }
        return (E) currentPosition.getElem();
    }
}
