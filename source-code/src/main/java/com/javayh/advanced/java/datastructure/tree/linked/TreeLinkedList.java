package com.javayh.advanced.java.datastructure.tree.linked;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-07
 */
public class TreeLinkedList<E> implements Tree<E> {

    /**根节点*/
    private E element;

    /**父亲、长子及最大的弟弟*/
    private TreeLinkedList<E> parent, firstChild, nextSibling;

    public TreeLinkedList() {
        this(null,null,null,null);
    }

    public TreeLinkedList(E element, TreeLinkedList<E> parent, TreeLinkedList<E> firstChild, TreeLinkedList<E> nextSibling) {
        this.element = element;
        this.parent = parent;
        this.firstChild = firstChild;
        this.nextSibling = nextSibling;
    }

    @Override
    public E getElem() {
        return element;
    }

    @Override
    public E setElem(E element) {
        E old = this.element;
        this.element = element;
        return old;
    }

    @Override
    public TreeLinkedList<E> getParent() {
        return this.parent;
    }

    @Override
    public TreeLinkedList<E> getFirstChild() {
        return this.firstChild;
    }

    /**
     * <p>
     *     返回当前节点的最大弟弟；若没有弟弟，则返回null
     * </p>
     * @return com.javayh.advanced.java.datastructure.tree.linked.TreeLinkedList<E>
     */
    @Override
    public TreeLinkedList<E> getNextSibling() {
        return this.nextSibling;
    }

    /**
     * 首先通过 firstChild 引用找出根节点的长子，并沿着 nextSibling 引用顺
     * 次找到其余的孩子，递归地统计出各子树的规模。最后，只要将所有子树的规模累加起来，再计入
     * 根节点本身，就得到了整棵树的规模。当遇到没有任何孩子的节点（即原树的叶子）时，递归终止。
     * 如果不计入递归调用，该算法在每个节点上只需花费常数时间，因此若树的规模为 n，则总的
     * 时间复杂度为 O(n)。读者也许注意到了，实际上，这一算法也能够在 O(n)时间内统计出树中所有子
     * 树的规模
     */
    @Override
    public int size() {
        //当前节点也是自己的后代
        int size = 1;
        //从长子开始
        TreeLinkedList<E> subtree = firstChild;
        while (null != subtree)
        {   // 累加
            size += subtree.size();
            // 所有孩子的后代数目
            subtree = subtree.getNextSibling();
        }//即可得到当前节点的后代总数
        return size;
    }

    /**
     * 法 getHeight(v)也是首先通过 firstChild 引用找出根节点的长子，并沿着 nextSibling
     * 引用顺次找到其余的孩子，递归地计算出各子树的高度。最后，只要找出所有子树的最大高度，再
     * 计入根节点本身，就得到了根节点的高度（即树高）
     */
    @Override
    public int getHeight() {
        int height = -1;
        //从长子开始
        TreeLinkedList<E> subtree = firstChild;
        while (null != subtree){
            //在所有孩子中取最大的高度
            height = Math.max(height,subtree.getHeight());
            subtree = subtree.getNextSibling();
        }
        return height+1;
    }

    @Override
    public int getDepth() {
        int depth = 0;
        //从父亲开始
        TreeLinkedList<E> p = parent;
        while (null != p) {
            depth++;
            //访问各个真祖先
            p = p.getParent();
        }//真祖先的数目，即为当前节点的深度
        return depth;
    }
}
