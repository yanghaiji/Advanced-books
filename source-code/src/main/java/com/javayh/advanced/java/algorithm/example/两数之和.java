package com.javayh.advanced.java.algorithm.example;

import java.util.Arrays;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2020-09-27
 */
public class 两数之和 {

    /**
     * 给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那 两个 整数，并返回他们的数组下标。
     * 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素不能使用两遍。
     * 示例:
     * 给定 nums = [2, 7, 11, 15], target = 9
     * 因为 nums[0] + nums[1] = 2 + 7 = 9
     * 所以返回 [0, 1]
     */
    public static void main(String[] args) {
        int[] nums = new int[]{2, 8, 11, 15, 7};
        int target = 9;
        int[] targetNum = new int[2];
        for (int num : nums) {
            for (int j = 1; j < nums.length; j++) {
                if (target == num + nums[j]) {
                    targetNum[0] = num;
                    targetNum[1] = nums[j];
                }
            }
        }
        System.out.println("target 下标:"+ Arrays.toString(targetNum));
    }

}
