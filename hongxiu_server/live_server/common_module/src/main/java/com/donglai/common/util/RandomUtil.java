package com.donglai.common.util;

import com.donglai.common.constant.PathConstant;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class RandomUtil {

    public static Random random = new Random();

    public static int getRandomInt(int begin, int end, Long seed) {
        if (begin == end) return begin;
        if (seed == null) random = new Random();
        else random.setSeed(seed);
        return random.nextInt(end - begin + 1) + begin;
    }

    /**
     * 1.随机获取指定位数的验证码
     *
     * @param length 验证码长度
     */
    public static String getAuthCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        if (length <= 0) {
            sb.append(-1);
        } else {
            for (int i = 0; i < length; i++)
                sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public static String randomDefaultAvatar(){
        int randomInt = getRandomInt(1, 20, null);
        return PathConstant.DEFAULT_AVATAR_BASE_PATH + randomInt + ".jpg";
    }

    public static String getRandomJianHan(int len) {
        String randomName = "";
        for (int i = 0; i < len; i++) {
            String str = null;
            int hightPos, lowPos; // 定义高低位
            Random random = new Random();
            hightPos = (176 + Math.abs(random.nextInt(39))); // 获取高位值
            lowPos = (161 + Math.abs(random.nextInt(93))); // 获取低位值
            byte[] b = new byte[2];
            b[0] = (new Integer(hightPos).byteValue());
            b[1] = (new Integer(lowPos).byteValue());
            try {
                str = new String(b, "GBK"); // 转成中文
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
            randomName += str;
        }
        return randomName;
    }
}
