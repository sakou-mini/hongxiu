package com.donglai.common.util;

import java.util.Random;

public class RandomUtil {

    public static Random random = new Random();

    public static int getRandomInt(int begin, int end, Long seed) {
        if (begin == end) return begin;
        if(seed == null) random = new Random();
        else random.setSeed(seed);
        return random.nextInt(end - begin + 1) + begin;
    }
}
