package com.javayh.advanced.algorithm.search;

/**
 * <p>
 * 线性查找
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-12-28 8:01 PM
 */
public class SeqSearch {
    public static void main(String[] args) {
        int[] array =  {1,8, 10, 89, 1000, 1234};

        int i = seqSearch(array, 809);
        if(i == 1){
            System.out.println("array 中查到目标数据");
        }else {
            System.out.println("array 中未查到目标数据");
        }

    }

    /**
     *
     * @param array 目标数据
     * @param num 需要查询的数据
     */
    private static int seqSearch(int[] array, int num) {
        // flag = 1 表示查找到 ,否则为为查找到
        int flag = 0;
        for (int i = 0; i < array.length-1; i++) {
            if (num == array[i]) {
                flag = 1;
                break;
            }
        }
        return flag;
    }
}
