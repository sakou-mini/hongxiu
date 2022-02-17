package com.donglai.common.util;

import com.alibaba.fastjson.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class PasswordUtil {

    public static final String TOKEN_KEY = "6c4c8376-3ac9-4092";

    //可以加密和解密的算法(对称加密)
    //1.加密
    public static String encodePassword(String password) {
        try {
            JSONObject json = new JSONObject();
            json.put("password", password);
            json.put("times", System.currentTimeMillis());
            json.put("token", getMD5(password + json.getString("times") + TOKEN_KEY));
            return encodeBase64(json.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkEncodePassword(String password, String encodePassword) {
        boolean equals = Objects.equals(password, decodePassword(encodePassword));
        return equals;
    }

    //解密
    public static String decodePassword(String str) {
        if (StringUtils.isNullOrBlank(str)) return "";
        try {
            String decode = decodeBase64(str);
            JSONObject jsonObject = JSONObject.parseObject(decode);
            return jsonObject.getString("password");
        } catch (Exception e) {
            return null;
        }
    }

    private static String encodeBase64(String str) {
        return new String(org.apache.commons.codec.binary.Base64.encodeBase64(str.getBytes()));
    }

    private static String decodeBase64(String str) {
        return new String(org.apache.commons.codec.binary.Base64.decodeBase64(str.getBytes()));
    }

    private static String getMD5(String str) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes());
        return new BigInteger(1, md.digest()).toString(16);
    }
}
