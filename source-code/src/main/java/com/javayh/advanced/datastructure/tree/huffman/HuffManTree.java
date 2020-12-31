package com.javayh.advanced.datastructure.tree.huffman;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 赫夫曼树
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-12-31 11:50 AM
 */
public class HuffManTree {
    public static void main(String[] args) {
        int[] array = {13, 7, 8, 3, 29, 6, 1 };
        Node huffmanTree = createHuffmanTree(array);
        preOrder(huffmanTree);
    }

    /**
     * 编写一个前序遍历的方法
     * @param root
     */
    public static void preOrder(Node root) {
        if (root != null) {
            root.preOrder();
        } else {
            System.out.println("是空树，不能遍历~~");
        }
    }

    /**
     * <p>
     *       构建一个赫夫曼树
     * </p>
     * @version 1.0.0
     * @since 12/31/2020
     * @param array     需要构建的数组
     * @return com.javayh.advanced.datastructure.tree.huffman.Node 构建后的赫夫曼树
     */
    public static Node createHuffmanTree(int[] array) {
        // 第一步为了操作方便
        // 1.  遍历 arr 数组
        // 2.  将 arr 的每个元素构成成一个 Node
        // 3.  将 Node 放入到 ArrayList 中
        List<Node> nodes = new ArrayList<Node>();
        for (int value : array) {
            nodes.add(new Node(value));
        }
        while (nodes.size() > 1) {
            //排序 从小到大
            Collections.sort(nodes);
            System.out.println("nodes =" + nodes);
            //取出根节点权值最小的两颗二叉树
            //(1) 取出权值最小的结点（二叉树）
            Node leftNode = nodes.get(0);
            //(2) 取出权值第二小的结点（二叉树）
            Node rightNode = nodes.get(1);
            //(3)构建一颗新的二叉树
            Node parent = new Node(leftNode.getValue() + rightNode.getValue());
            parent.setLeft(leftNode);
            parent.setRight(rightNode);
            //(4)从 ArrayList 删除处理过的二叉树
            nodes.remove(leftNode);
            nodes.remove(rightNode);
            //(5)将 parent 加入到 nodes
            nodes.add(parent);
        }
        return nodes.get(0);

    }

}

@Getter
@Setter
class Node implements Comparable<Node> {

    private int value;
    private Node left;
    private Node right;

    Node(int value) {
        this.value = value;
    }

    /**
     * 前序遍历
     */
    public void preOrder() {
        System.out.println(this);
        if (this.left != null) {
            this.left.preOrder();
        }
        if (this.right != null) {
            this.right.preOrder();
        }
    }

    @Override
    public int compareTo(Node o) {
        // 表示从小到大排序
        return this.value - o.value;
    }

    @Override
    public String toString() {
        return "Node{" +
                "value=" + value +
                '}';
    }

}
