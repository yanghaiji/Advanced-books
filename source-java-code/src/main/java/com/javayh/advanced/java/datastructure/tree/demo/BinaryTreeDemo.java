package com.javayh.advanced.java.datastructure.tree.demo;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 二叉树遍历应用实例(前序,中序,后序)
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-12-30 11:13 AM
 */
public class BinaryTreeDemo {
    public static void main(String[] args) {
        /*
        * 构建二叉树的模型
        *               A
        *            /     \
        *           B       C
        *                /     \
        *               D       E
        */
        /*测试二叉树的三种遍历方式*/
        BinaryTree binaryTree = new BinaryTree();
        HeroNode root = new HeroNode(1,"A");
        HeroNode node1 = new HeroNode(2,"B");
        HeroNode node2 = new HeroNode(3,"C");
        HeroNode node3 = new HeroNode(4,"D");
        HeroNode node4 = new HeroNode(5,"E");
        root.setLeft(node1);
        root.setRight(node2);
        node2.setLeft(node3);
        node2.setRight(node4);
        binaryTree.setRoot(root);
        //前序遍历
        System.out.println("前序遍历~~~");
        binaryTree.preOrder();
        //中序遍历
        System.out.println("中序遍历~~~");
        binaryTree.infixOrder();
        //后序遍历
        System.out.println("后序遍历~~~");
        binaryTree.postOrder();

        /*二叉树查找指定的结点*/

        //前序查找
        System.out.println("前序查找~~~");
        HeroNode preSearch = binaryTree.preSearch(5);
        System.out.println("前序查找~~~" + preSearch);
        //中序查找
        System.out.println("中序查找~~~");
        HeroNode infixSearch = binaryTree.infixSearch(5);
        System.out.println("中序查找~~~" + infixSearch);
        //后序遍查找
        System.out.println("后序查找~~~");
        HeroNode postSearch = binaryTree.postSearch(5);
        System.out.println("后序查找~~~" + postSearch);
    }
}

class BinaryTree{
    private HeroNode root;

    public void setRoot(HeroNode root) {
        this.root = root;
    }

    @Override
    public String toString() {
        return "BinaryTree[" +
                "root=" + root +
                ']';
    }

    /**前序遍历*/
    public void preOrder(){
        if(root != null){
            this.root.preOrder();
        }else {
            System.out.println("二叉树为空~~~~");
        }
    }
    /**中序遍历*/
    public void infixOrder(){
        if(root != null){
            this.root.infixOrder();
        }else {
            System.out.println("二叉树为空~~~~");
        }
    }
    /**后序遍历*/
    public void postOrder(){
        if(root != null){
            this.root.postOrder();
        }else {
            System.out.println("二叉树为空~~~~");
        }
    }

    /*二叉树查找指定节点*/

    /**前序遍历*/
    public HeroNode preSearch(int no){
        if(root != null){
            return this.root.preSearch(no);
        }else {
            return null;
        }
    }
    /**中序遍历*/
    public HeroNode infixSearch(int no){
        if(root != null){
            return this.root.infixSearch(no);
        }else {
            return null;
        }
    }
    /**后序遍历*/
    public HeroNode postSearch(int no){
        if(root != null){
            return this.root.postSearch(no);
        }else {
            return null;
        }
    }
}

@Getter
@Setter
class HeroNode {
    private int no;
    private String name;
    private HeroNode left;
    private HeroNode right;

    public HeroNode(int no, String name) {
        this.no = no;
        this.name = name;
    }
    @Override
    public String toString() {
        return "HeroNode [no=" + no + ", name=" + name + "]";

    }
    /**前序遍历*/
    public void preOrder(){
        //输出父节点
        System.out.println(this);
        //递归实现左侧遍历
        if(this.left != null){
            this.left.preOrder();
        }
        //递归实现右侧遍历
        if(this.right != null){
            this.right.preOrder();
        }
    }
    /**中序遍历*/
    public void infixOrder(){
        //递归实现左侧遍历
        if(this.left != null){
            this.left.infixOrder();
        }
        //输出父节点
        System.out.println(this);
        //递归实现右侧遍历
        if(this.right != null){
            this.right.infixOrder();
        }
    }

    /**后序遍历*/
    public void postOrder(){
        //递归实现左侧遍历
        if(this.left != null){
            this.left.infixOrder();
        }
        //递归实现右侧遍历
        if(this.right != null){
            this.right.infixOrder();
        }
        //输出父节点
        System.out.println(this);
    }

    /**前序查找*/
    public HeroNode preSearch(int no){
        //如果当前节点为要找的节点直接返回
        System.out.println("前序查找");
        if(this.no == no){
            return this;
        }
        HeroNode res = null;
        //递归前序查找
        if(this.left != null){
           res = this.left.preSearch(no);
        }
        if(res != null){
            return res;
        }
        if(this.right != null){
            res = this.right.preSearch(no);
        }
        return res;
    }

    /**中序序查找*/
    public HeroNode infixSearch(int no){
        //如果当前节点为要找的节点直接返回
        HeroNode res = null;
        //递归前序查找
        if(this.left != null){
            res = this.left.infixSearch(no);
        }
        if(res != null){
            return res;
        }
        System.out.println("中序序查找");
        if(this.no == no){
            return this;
        }
        if(this.right != null){
            res = this.right.infixSearch(no);
        }
        return res;
    }

    /**后序序查找*/
    public HeroNode postSearch(int no){
        //如果当前节点为要找的节点直接返回
        HeroNode res = null;
        //递归前序查找
        if(this.left != null){
            res = this.left.postSearch(no);
        }
        if(res != null){
            return res;
        }
        if(this.right != null){
            res = this.right.postSearch(no);
        }
        if(res != null){
            return res;
        }
        System.out.println("后序序查找");
        if(this.no == no){
            return this;
        }
        return null;
    }
}