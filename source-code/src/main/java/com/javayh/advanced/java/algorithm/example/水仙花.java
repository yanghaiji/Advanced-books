package com.javayh.advanced.java.algorithm.example;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-25
 */
public class 水仙花 {
    public static void main(String[] args) {
        //打印出所有的 "水仙花数 "，所谓 "水仙花数 "是指一个三位数，其各位数字立方和等于该数本身。
        // 例如：153是一个 "水仙花数 "，因为153=1的三次方＋5的三次方＋3的三次方。
        System.out.println(narcissus(153, 3));
        System.out.println(narcissus(370 , 3));
        System.out.println(narcissus(407 , 3));
        System.out.println(narcissus(371, 3));
        System.out.println(narcissus(334, 3));
    }

    /**
     *
     * @param num 原数据
     * @param pow 次方数
     * @return
     */
    static boolean narcissus(Integer num, Integer pow){
        int x , y , z = 0;
        //计算出十位与各位的数
        int ten = num % 100;
        //计算出百位
        x = num / 100;
        //计算出十位
        y = ten/10;
        //计算出各位
        z = ten % 10;
        int sum = (int) (Math.pow(x,pow) +  Math.pow(y,pow) +  Math.pow(z,pow));
        System.out.println("计算前的数据:"+num);
        System.out.println("计算水仙花数的和为:"+sum);
        return sum == num;
    }
}
