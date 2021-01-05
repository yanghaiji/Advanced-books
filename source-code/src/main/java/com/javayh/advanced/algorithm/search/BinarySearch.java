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
        //递归的查找
        int i = binaryRecursionSearch(array, 1000);
        if(i > 0){
            System.out.printf("array [%d] = %d",i,array[i]);
        }else {
            System.out.println("未找到目标数据");
        }
        System.out.println();
        //非递归的查找
        int i2 = binarySearch(array, 89);
        if(i2 > 0){
            System.out.printf("array [%d] = %d",i2,array[i2]);
        }else {
            System.out.println("未找到目标数据");
        }
    }

    public static int binarySearch(int[] array, int key){
        int left = 0;
        int right = array.length;
        while (left <= right) {
            int mid = (right + left) / 2;
            if(array[mid] == key){
                return mid;
            }
            // 如果大于说明需要向左查找
            else if(array[mid] > key){
                right = mid-1;
            }
            else {
                left = mid +1;
            }
        }
        return -1;
    }

    /**
     * 进行一个方法的重载
     * @param array 原数组
     * @param key   需要查找的数值
     * @return      返回元素的下标
     */
    public static int binaryRecursionSearch(int[] array, int key){
        return binaryRecursionSearch(array,0,array.length,key);
    }

    /**
     * 二分查找
     *
     * @param array 原数组
     * @param start 开始下标
     * @param end   结束下标
     * @param key   需要查找的数值
     */
    private static int binaryRecursionSearch(int[] array, int start, int end, int key) {
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
            return binaryRecursionSearch(array, num + 1, end, key);
        }
        // 如果要查找的数据 < array[num] , 说明 key 在num的左侧
        // 所以需要将查找的范围缩小到 num -1 的位置，起始位置不变
        else if (key < array[num]) {
            return binaryRecursionSearch(array, start, num - 1, key);
        }
        return -1;
    }
}



