package com.javayh.advanced.algorithm.sort;

import com.javayh.advanced.algorithm.SortUtils;

/**
 * <p>
 * 选择排序
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-12-23 5:34 PM
 */
public class SelectSort {
    public static void main(String[] args) {
        //int[] array = {101, 34, 119, 1, -1, 90, 123};
        int[] array = SortUtils.createArray(80000, 8000000);
        SortUtils.sysBefore(array);
        selectSort(array);
        SortUtils.sysAfter(array);
    }

    /**
     * 选择排序
     */
    private static void selectSort(int[] array){
        int length = array.length - 1;
        for (int i = 0; i < length; i++) {
            int minIndex = i;
            int min = array[i];
            for (int j = i +1; j < length; j++) {
                // 说明假定的最小值，并不是最小
                if (min > array[j]) {
                    // 重 置 min
                    min = array[j];
                    // 重 置 minIndex
                    minIndex = j;
                }
            }
            // 将最小值，放在 arr[0],  即交换
            if (minIndex != i) {
                array[minIndex] = array[i];
                array[i] = min;
            }
        }
    }

}
