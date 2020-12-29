package com.javayh.advanced.algorithm.sort;

import com.javayh.advanced.algorithm.SortUtils;

/**
 * <p>
 *  插入排序
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-12-24 10:04 AM
 */
public class InsertSort {
    public static void main(String[] args) {
        //int[] array = {101, 34, 119, 1};
        int[] array = SortUtils.createArray(800, 20000);
        SortUtils.sysBefore(array);
        insertSort(array);
        SortUtils.sysAfter(array);
    }

    private static void insertSort(int[] array) {
        int insertVal ;
        int insertIndex ;
        for (int i = 1; i < array.length; i++) {
            insertVal = array[i];
            //获取 array[i] 之前的数据
            insertIndex = i-1;
            // 给 insertVal 找寻插入的位置
            // 1. insertIndex >= 0 保证在给insertVal 找插入的位置，不越界
            // 2. insertVal < array[insertIndex] 待插入的数。还没找到插入的位置
            // 3. 将array[insertIndex] 后移
            while (insertIndex >=0 && insertVal < array[insertIndex]){
                array[insertIndex + 1] = array[insertIndex];
                insertIndex--;
            }
            // 当退出 while 循环时，说明插入的位置找到, insertIndex + 1
            // 举例：理解不了，我们一会 debug
            //这里我们判断是否需要赋值
            if(insertIndex + 1 != i) {
                array[insertIndex + 1] = insertVal;
            }
        }
    }
}

