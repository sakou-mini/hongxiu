package com.donglai.account.process;

import com.donglai.common.constant.AccountRedisConstant;
import com.donglai.common.constant.EmailPhoneConstant;
import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.common.service.RedisService;
import com.donglai.common.util.RandomUtil;
import com.donglai.protocol.Constant;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AuthCodeProcess {
    //验证码提交时间
    private static ConcurrentHashMap<String, Long> numberTimeRecord = new ConcurrentHashMap();// 电话号码发送间

    private static final RedisService redisService = SpringApplicationContext.getBean((RedisService.class));

    /*1.存储验证码，有效期5分钟*/
    public static void saveAuthCode(String bindNumber, String code, Constant.AuthCodeType type) {
        String authCodeKey = AccountRedisConstant.getPhoneAuthCodeKey(type, bindNumber);
        redisService.set(authCodeKey, code, TimeUnit.MILLISECONDS.toSeconds(EmailPhoneConstant.codeOverdueTime));
    }

    /*2.获取验证码*/
    public static String getAuthCode(String bindNumber, Constant.AuthCodeType type) {
        String authCodeKey = AccountRedisConstant.getPhoneAuthCodeKey(type, bindNumber);
        return (String) redisService.get(authCodeKey);
    }

    /*3.清除验证码*/
    public static void cleanAuthCode(String bindNumber, Constant.AuthCodeType type) {
        String authCodeKey = AccountRedisConstant.getPhoneAuthCodeKey(type, bindNumber);
        redisService.del(authCodeKey);
    }

    /*4.发送短信验证码*/
    public static Constant.ResultCode dealSendPhoneAuthCode(String phoneNumber, Constant.AuthCodeType type) {
        Long lastTime = numberTimeRecord.getOrDefault(phoneNumber, 0L);
        Long now = System.currentTimeMillis();
        if (!(now - lastTime > EmailPhoneConstant.interval))
            return Constant.ResultCode.HIGH_FREQUENCY;
        String authCode = RandomUtil.getAuthCode(4);
        //TODO 发送验证码
        boolean result = true;
        if (result) {
            log.info("已成功发送了手机验证码");
            numberTimeRecord.put(phoneNumber, now);
            //存储验证码到redis
            saveAuthCode(phoneNumber, authCode, type);
            return Constant.ResultCode.SUCCESS;
        } else {
            return Constant.ResultCode.UNKNOWN;
        }
    }
}
