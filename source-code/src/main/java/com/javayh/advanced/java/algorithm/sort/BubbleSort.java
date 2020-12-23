package com.javayh.advanced.java.algorithm.sort;

import com.javayh.advanced.java.algorithm.SortUtils;

/**
 * <p>
 * 冒泡排序
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-12-23 2:40 PM
 */
public class BubbleSort {

    public static void main(String[] args) {
        //int[] array = {3, 9, -1, 10, -2};
        //System.out.println("排序前的数据: "+Arrays.toString(args));
        //创建要给80000 个的随机数组
        int[] array = SortUtils.createArray(80000, 8000000);
        SortUtils.sysBefore(array);
        bubbleSort(array);
        SortUtils.sysAfter(array);

    }

    private static void bubbleSort(int[] array) {
        //临时变量
        int temp;
        //  标识变量，表示是否进行过交换
        boolean flag = false;
        for (int i = 0; i < array.length-1; i++) {
            for (int j = 0; j < array.length - 1 -i; j++) {
                if(array[j] > array[j+1]){
                    flag = true;
                    temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
            // 在一趟排序中，一次交换都没有发生过
            if (!flag) {
                break;
            }
            // 重置 flag!!!, 进行下次判断
            else {
                flag = false;
            }
        }
    }

}
