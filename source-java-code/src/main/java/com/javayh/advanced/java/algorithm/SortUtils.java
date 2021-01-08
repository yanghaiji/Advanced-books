package com.javayh.advanced.java.algorithm;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Arrays;
import java.util.Date;

/**
 * <p>
 * 记录耗时
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-12-23 5:39 PM
 */
public class SortUtils {

    /**
     * 排序前的时间
     * @param array
     */
    public static void sysBefore(int[] array){
        System.out.println("排序前的数据: "+ Arrays.toString(array));
        String before = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        System.out.println("排序前的时间: "+before);
    }

    /**
     * 排序后的时间
     * @param array
     */
    public static void sysAfter(int[]  array){
        System.out.println("排序后的数据: "+Arrays.toString(array));
        String after = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        System.out.println("排序后的时间: "+after);

    }

    /**
     *
     * @param init 初始化数据的长度
     * @param size 随机数的范围
     * @return
     */
    public static int[] createArray(int init,int size){
        //创建要给80000 个的随机数组
        int[] array = new int[init];
        for (int i = 0; i < init; i++) {
            array[i] = (int) (Math.random()* size);
        }
        return array;
    }
}
