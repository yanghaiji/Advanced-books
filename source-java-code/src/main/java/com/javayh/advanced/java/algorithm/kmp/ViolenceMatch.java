package com.javayh.advanced.java.algorithm.kmp;

/**
 * <p>
 * 暴力破解
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2021-01-06
 */
public class ViolenceMatch {
    public static void main(String[] args) {
        String str1 = "Java JavaH JavaHelloJavaWord";
        String str2 = "HelloJava";
        int i = violenceMatch(str1, str2);
        System.out.println("index = "+i);
    }

    /**
     * 暴力破解两个字符串是否相等
     *
     * @param str1 源字符串
     * @param str2 字串
     * @return 匹配成功后的下标
     */
    public static int violenceMatch(String str1, String str2) {
        char[] s1 = str1.toCharArray();
        char[] s2 = str2.toCharArray();
        int s1Len = s1.length;
        int s2Len = s2.length;
        int i = 0; // i 索引指向 s1
        int j = 0; // j 索引指向 s2
        while (i < s1Len && j < s2Len) {//  保证匹配时，不越界
            if (s1[i] == s2[j]) {//匹配成功
                i++;
                j++;
            } else {//匹配失败
                i = i - (j - 1);
                j = 0;
            }
        }
        //判断是否匹配成功
        if (j == s2Len) {
            return i - j;
        } else {
            return -1;
        }
    }
}