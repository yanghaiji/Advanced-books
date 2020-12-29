package com.javayh.advanced.algorithm.example.sort;

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
public class 插入排序 {

    private static final int[] array =
            {49, 38, 65, 97, 76, 13, 27, 78, 34, 12, 15, 35, 25, 53, 51};

    public static void main(String[] args) {
        for (int i = 1; i < array.length; i++) {
            int temp = array[i];
            int j = i - 1;
            for (; j >= 0 && array[j] > temp; j--) {
                //将大于temp的值整体后移一个单位
                array[j + 1] = array[j];
            }
            array[j + 1] = temp;
        }
        System.out.println(Arrays.toString(array));
    }
}
