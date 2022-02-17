package com.donglai.web.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class DuoCaiPasswordUtil {

    public static final char[] charr = "~!@#$%^&*.?".toCharArray();

    public static String generatedPwd() {
        String str1 = RandomStringUtils.random(4, true, true);
        String str2 = randomByStrArr(2);
        ArrayList<char[]> chars = Lists.newArrayList((str1 + str2).toCharArray());
        Collections.shuffle(chars);
        StringBuilder sb = new StringBuilder();
        for (char[] aChar : chars) {
            sb.append(aChar);
        }
        return sb.toString();
    }

    public static String randomByStrArr(int len) {
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int x = 0; x < len; ++x) {
            sb.append(charr[r.nextInt(charr.length)]);
        }
        return sb.toString();
    }
}
