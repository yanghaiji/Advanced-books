package com.javayh.advanced.java.algorithm.example;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-25
 */
public class 取模运算 {
    public static void main(String[] args) {
        // 判断1-20之间有多少个素数，并输出所有素数
        // 不能被2整除的数
        System.out.println("个数：" + mouldTaking(20));

    }

    static int mouldTaking(Integer num) {
        AtomicInteger sum = new AtomicInteger(0);
        for (int i = 1; i < num; i++) {
            if (i % 2 != 0) {
                System.out.println("素数:" + i);
                sum.incrementAndGet();
            }
        }
        return sum.get();
    }

}
