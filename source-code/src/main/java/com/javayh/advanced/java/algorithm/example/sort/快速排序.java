package com.javayh.advanced.java.algorithm.example.sort;

import java.util.Arrays;

/**
 * <p>
 *
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-12-09 11:07 AM
 */
public class 快速排序 {

    /**
     * 实现排序
     * @param array 原数据
     * @param begin 起始位置
     * @param end   截至位置
     */
    static void sort(int[] array,int begin,int end){
        //1. 开始(i)和末尾(j)的没有相遇之前比较各个元素与基准值的大小
        if(begin < end){
            //选择基准数
            int base = array[begin];
            // 左标记
            int i = begin;
            //右标记
            int j = end;
            while (i < j){
                //从左往右 元素比基准数大
                while (i < j && array[j] > base){
                    //右标记 -- ，直到找到第一个比基准数小的
                    j--;
                }
                if(i < j){
                    //交换右扫描第一个基准数小的
                   array[i]=array[j];
                   //i标记右移一位
                   i++;
                }
                System.out.println(Arrays.toString(array));
                //从左往右扫描
                while (i < j && array[i] < base){
                    //左侧标记++，直到找到第一个比基准值大的，停止扫描
                    i++;
                }
                if(i < j){
                    //交换左扫描第一个比基准数值大的数
                   array[j] = array[i];
                   j--; // j 标记左移一位
                }
                System.out.println(Arrays.toString(array));
            }// 此时基准值左右两侧相对有序
            //此时i为中间位置，此时说明已经相遇
            array[i] = base;
            //左侧按照快排序的思路
            sort(array,begin,i-1);
            //右侧按照快排序的思路
            sort(array,i+1,end);
        }
    }

    public static void main(String[] args) {
        int[] array = new int[]{10,3,7,9,12,4,2};
        sort(array,0,array.length-1);
        System.out.println(Arrays.toString(array));
    }
}
