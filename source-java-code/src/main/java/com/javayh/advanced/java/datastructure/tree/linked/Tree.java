package com.javayh.advanced.java.datastructure.tree.linked;

/**
 * <p>
 * 树结构顶级接口
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-07
 */
public interface Tree<E> {
    /**
     * 返回当前节点存放的对象
     *
     * @return
     */
    E getElem();

    /**
     * 将element 存放到当前节点，并返回此前对象
     *
     * @param element
     * @return
     */
    E setElem(E element);

    /**
     * 返回当前节点的父节点
     *
     * @return
     */
    TreeLinkedList<E> getParent();

    /**
     * 返回当前节点的长子
     *
     * @return
     */
    TreeLinkedList<E> getFirstChild();

    /**
     * 返回当前节点的最大弟弟
     *
     * @return
     */
    TreeLinkedList<E> getNextSibling();

    /**
     * 返回当前节点后代元素的数目，即以当前节点为根的子树的规模
     *
     * @return
     */
    int size();

    /**
     * 返回当前节点的高度
     *
     * @return
     */
    int getHeight();

    /**
     * 返回当前节点的深度
     *
     * @return
     */
    int getDepth();
}
