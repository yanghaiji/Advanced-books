package com.javayh.advanced.java.algorithm.sort;

import java.util.Arrays;

/**
 * <p>
 * 堆排序
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-12-30 8:28 PM
 */
public class HeapSort {
    public static void main(String[] args) {
        //要求将数组进行升序排序
        int arr[] = {4, 6, 8, 5, 9};
        heapSort(arr);
        System.out.println("堆排序后："+ Arrays.toString(arr));
    }

    public static void heapSort(int arr[]) {
        int temp = 0;
        System.out.println("堆排序!!");
        //	//分步完成
        //adjustHeap(arr, 1, arr.length);
        //System.out.println("第一次" + Arrays.toString(arr)); // 4, 9, 8, 5, 6

        //adjustHeap(arr, 0, arr.length);
        //System.out.println("第 2 次" + Arrays.toString(arr)); // 9,6,8,5,4
        //完成我们最终代码
        //将无序序列构建成一个堆，根据升序降序需求选择大顶堆或小顶堆
        for (int i = arr.length / 2 - 1; i >= 0; i--) {
            adjustHeap(arr, i, arr.length);
        }

        /*
        * 2).将堆顶元素与末尾元素交换，将最大元素"沉"到数组末端;
        3).重新调整结构，使其满足堆定义，然后继续交换堆顶元素与当前末尾元素，反复执行调整+交换步骤，直到整个序列有序。
        */
        for (int j = arr.length - 1; j > 0; j--) {
            //交换
            temp = arr[j];
            arr[j] = arr[0];
            arr[0] = temp;
            adjustHeap(arr, 0, j);
        }

    }
    //将一个数组(二叉树), 调整成一个大顶堆

    /**
     * 功能： 完成 将 以 i 对应的非叶子结点的树调整成大顶堆
     * 举例	int arr[] = {4, 6, 8, 5, 9}; => i = 1 => adjustHeap => 得 到 {4, 9, 8, 5, 6}
     * 如果我们再次调用	adjustHeap 传入的是 i = 0 => 得到 {4, 9, 8, 5, 6} => {9,6,8,5, 4}
     *
     * @param arr    待调整的数组
     * @param i      表示非叶子结点在数组中索引
     * @param length 表示对多少个元素继续调整， length  是在逐渐的减少
     */
    private static void adjustHeap(int[] arr, int i, int length) {
        int temp = arr[i];//先取出当前元素的值，保存在临时变量
        //开始调整
        //说明
        //1. k = i * 2 + 1 k 是 i 结点的左子结点
        for (int k = i * 2 + 1; k < length; k = k * 2 + 1) {
            if (k + 1 < length && arr[k] < arr[k + 1]) { //说明左子结点的值小于右子结点的值
                k++; // k  指向右子结点
            }
            if (arr[k] > temp) {
                //如果子结点大于父结点
                arr[i] = arr[k]; //把较大的值赋给当前结点
                i = k; //!!! i 指向 k,继续循环比较
            } else {
                break;//!
            }
        }
        //当 for 循环结束后，我们已经将以 i 为父结点的树的最大值，放在了 最顶(局部)
        arr[i] = temp;//将 temp 值放到调整后的位置
    }

}