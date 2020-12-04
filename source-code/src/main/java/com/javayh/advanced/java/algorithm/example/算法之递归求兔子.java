package com.javayh.advanced.java.algorithm.example;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-21
 */
public class 算法之递归求兔子 {

    /**
     * 有一对兔子，从出生后第3个月起每个月都生一对兔子，小兔子长到第3个月后每个月又生一对兔子，
     * 假如兔子都不死，问每个月的兔子总数为多少？
     * <p>
     * 分析
     * 1	2	3	4	5	6	7
     * 种兔	1	1	1	1	2	3	5
     * 小兔	0	0	1	2	3	5	8
     * 总数	1	1	2	3	5	8	13
     * 当月的总数 = 前两个月的和
     */
    public static void main(String[] args) {
        for (int i = 0; i < 12; i++) {
            System.out.println(sum(i));
        }
    }

    /**
     * 求和
     *
     * @param mo
     * @return
     */
    static int sum(int mo) {
        //前两个月没有幼兔产生
        if (1 == mo || 2 == mo) {
            return 1;
        }
        return sum(mo - 1) + sum(mo - 2);
    }

}
