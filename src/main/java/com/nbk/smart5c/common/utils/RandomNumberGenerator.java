package com.nbk.smart5c.common.utils;

import java.util.Random;

/**
 * @Desc 随机数生成器
 */
public class RandomNumberGenerator {
    public static Integer generateRandomNumbers(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException("Invalid range or count");
        }

        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}
