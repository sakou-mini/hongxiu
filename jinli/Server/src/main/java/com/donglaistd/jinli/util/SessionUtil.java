package com.donglaistd.jinli.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.donglaistd.jinli.database.entity.User;
import org.apache.commons.codec.binary.Base64;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

public class SessionUtil {
	public static final String TOKEN_KEY = "ld23)IJO3jav93J:Lj3;c";
	//json md5 properties key
	public static final String KEY_USER_ID = "uid";
	public static final String KEY_TIMES = "times";
	public static final String KEY_TOKEN = "token";
	public static String getUserSession(User user) {
		try {
			long time = Instant.now().toEpochMilli();
			JSONObject json = new JSONObject();
			json.set(KEY_USER_ID, user.getId());
			json.set(KEY_TIMES, time);
			json.set(KEY_TOKEN, getMD5(user.getId() + time + TOKEN_KEY));
			return encodeBase64(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*解析为json*/
	public static JSONObject deCodeToJSon(String str){
		String decode = decodeBase64(str);
		return JSONUtil.parseObj(decode);
	}

	private static String encodeBase64(String str) {
		return new String(Base64.encodeBase64(str.getBytes()));
	}

	private static String decodeBase64(String str) {
		return new String(Base64.decodeBase64(str.getBytes()));
	}

	private static String getMD5(String str) throws NoSuchAlgorithmException {
		// 生成一个MD5加密计算摘要
		MessageDigest md = MessageDigest.getInstance("MD5");
		// 计算md5函数
		md.update(str.getBytes());
		// digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
		// BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
		return new BigInteger(1, md.digest()).toString(16);
	}


}
