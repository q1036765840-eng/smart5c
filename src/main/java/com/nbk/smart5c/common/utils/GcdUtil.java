package com.nbk.smart5c.common.utils;


import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Desc 未最大公约数工具类
 * @Date 2024-11-11
 * @Author jiaxuebin
 */
public class GcdUtil {
    // 递归方式求两个数的最大公约数
    public static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    // 递归方式求多个数的最大公约数
    public static int gcd(List<Integer> numbers) {
        if (numbers.size() == 0) return 0;
        int result = numbers.get(0);
        for (int i = 1; i < numbers.size(); i++) {
            result = gcd(result, numbers.get(i));
        }
        return result;
    }

    public static void main(String[] args) {
        List<Integer> numbers = Lists.newArrayList(45, 27, 15, 9);
        int gcdResult = gcd(numbers);
        System.out.println("最大公约数是: " + gcdResult);
    }
}