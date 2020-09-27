package com.javayh.advanced.java.algorithm.example;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-27
 */
public class 二分查找 {
    public static void main(String[] args) {
        int array[] = {3,5,11,17,21,23,28,30,32,50,64,78,81,95,101};
        System.out.println(binSerch(array,5,array.length-1,81));
    }

    /**
     *  递归实现
     * @param art 数组
     * @param start 开始下标
     * @param end   结束下标
     * @param key 需要查找的数值
     * @return
     */
    public static int binSerch(int art[],int start,int end ,int key){
        int mid = (end + start)/2;
        if (art[mid] == key){
            return mid;
        }
        if (start >= end){
            return -1;
        }else if(key > art[mid]){
            return binSerch(art,mid+1,end,key);
        }else if(key < art[mid]){
            return binSerch(art,start,mid-1,key);
        }
        return -1;
    }
}
