package com.javayh.advanced.java.algorithm.example.sort;

import java.util.Arrays;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-10-13
 */
public class 冒泡排序 {

    public static void main(String[] args) {
        int[] array = new int[]{2,10,8,12,5,1,89};
        //暂存值
        int temp = 0;
        //控制循环的次数
        for (int i = 0; i < array.length-1; i++) {
            //进行重排序  这里 array.length-1-i 减少循环次数
            for (int j = 0; j < array.length-1-i; j++) {
                if (array[j] > array[j + 1]) {
                    temp = array[j];
                    //调换前后位置
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        System.out.println("排序后的数据:"+ Arrays.toString(array));
    }
}
