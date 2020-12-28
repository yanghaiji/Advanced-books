package com.javayh.advanced.java.algorithm.sort;

import com.javayh.advanced.java.algorithm.SortUtils;

/**
 * <p>
 * 希尔排序
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-12-24 11:51 AM
 */
public class ShellSort {
    public static void main(String[] args) {
        int[] array = { 8, 9, 1, 7, 2, 3, 5, 4, 6, 0 };
        SortUtils.sysBefore(array);
        shellSort(array);
        SortUtils.sysAfter(array);
    }

    private static void shellSort(int[] array) {
        int temp = 0;
        for (int gap = array.length / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < array.length; i++) {
                // 遍历各组中所有的元素(共 gap 组，每组有个元素),  步长 gap
                for (int j = i - gap; j >= 0; j -= gap) {
                    // 如果当前元素大于加上步长后的那个元素，说明交换
                    if (array[j] > array[j + gap]) {
                        temp = array[j];
                        array[j] = array[j + gap];
                        array[j + gap] = temp;
                    }
                }
            }
        }
    }
}
