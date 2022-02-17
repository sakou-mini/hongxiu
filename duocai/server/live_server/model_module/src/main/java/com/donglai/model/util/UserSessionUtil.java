package com.donglai.model.util;

import com.alibaba.fastjson.JSONObject;
import com.donglai.model.db.entity.common.User;
import org.apache.commons.codec.binary.Base64;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

public class UserSessionUtil {
	public static final String TOKEN_KEY = "ld23)IJO3jav93J:Lj3;c";

	public static String getUserSession(User user) {
		try {
			JSONObject json = new JSONObject();
			json.put("uid", user.getId());
			json.put("accountId", user.getAccountId());
			json.put("times", Instant.now().toEpochMilli());
			json.put("token", getMD5(user.getAccountId() + json.getString("times") + TOKEN_KEY));
			return encodeBase64(json.toJSONString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*解析为json*/
	public static JSONObject deCodeToJSon(String str){
		String decode = decodeBase64(str);
		return JSONObject.parseObject(decode);
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
