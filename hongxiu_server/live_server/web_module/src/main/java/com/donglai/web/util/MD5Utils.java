package com.donglai.web.util;

import org.apache.shiro.crypto.hash.SimpleHash;

/*加密的工具类*/
public class MD5Utils {
    /*加密方式*/
    public static final String ALGORITHMNAME = "MD5";
    /*加盐*/
    public static final String SALT = "hongxiu";

    /*加密次数*/
    public static final Integer HASHITERATIONS = 2;

    public static String setMd5Crytography(String source) {
        SimpleHash hash = new SimpleHash(ALGORITHMNAME, source, SALT, HASHITERATIONS);
        return hash.toString();
    }
}
