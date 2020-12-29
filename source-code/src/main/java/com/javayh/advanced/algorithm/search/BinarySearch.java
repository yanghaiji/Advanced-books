package com.javayh.advanced.algorithm.search;

/**
 * <p>
 * 二分查找算法
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-12-28 8:21 PM
 */
public class BinarySearch {
    public static void main(String[] args) {
        int[] array = {1, 8, 10, 89, 1000, 1234};
        int i = binarySearch(array, 0, array.length, 1000);
        if(i > 0){
            System.out.printf("array [%d] = %d",i,array[i]);
        }else {
            System.out.println("未找到目标数据");
        }

    }

    /**
     * 二分查找
     *
     * @param array 原数组
     * @param start 开始下标
     * @param end   结束下标
     * @param key   需要查找的数值
     */
    private static int binarySearch(int[] array, int start, int end, int key) {
        // 去一半
        int num = (end + start) / 2;
        // 表示找到
        if (key == array[num]) {
            return num;
        }
        if (start >= end) {
            return -1;
        }
        // 如果要查找的数据 > array[num] , 说明 key 在num的右侧
        // 所以需要将 num + 1 ，而结束位置不动
        else if (key > array[num]) {
            return binarySearch(array, num + 1, end, key);
        }
        // 如果要查找的数据 < array[num] , 说明 key 在num的左侧
        // 所以需要将查找的范围缩小到 num -1 的位置，起始位置不变
        else if (key < array[num]) {
            return binarySearch(array, start, num - 1, key);
        }
        return -1;
    }
}
