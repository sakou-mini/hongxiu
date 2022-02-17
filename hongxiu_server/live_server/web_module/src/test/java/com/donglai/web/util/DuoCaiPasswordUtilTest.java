package com.donglai.web.util;

import com.donglai.common.util.PasswordUtil;
import com.donglai.web.WebBaseTest;
import org.junit.Test;

public class DuoCaiPasswordUtilTest extends WebBaseTest {
    @Test
    public void test() {
        for (int i = 0; i < 1000; i++) {
            String pwd = DuoCaiPasswordUtil.generatedPwd();
            System.out.println("加密前：" + pwd);
            String encodePassword = PasswordUtil.encodePassword(pwd);
            System.out.println("加密后：" + encodePassword);
            String decodePassword = PasswordUtil.decodePassword(encodePassword);
            System.out.println("解密后：" + decodePassword);
        }

    }
}
