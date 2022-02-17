package com.donglaistd.jinli.util;

import com.donglaistd.jinli.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncddeUtilTest extends BaseTest {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void test(){
        String pws = "123456";
        long startTime;
        for (int i = 0; i <200 ; i++) {
            startTime = System.currentTimeMillis();
            String token = "$2a$12$Usg4mia/AudvtJsGwYE3tOmkOQKvc.7Df/e2q9PGpMoCJKPVmdNM2";
            boolean matches = passwordEncoder.matches(pws, token);
            System.out.println(matches);
            System.err.println("匹配成功！耗时："+ (System.currentTimeMillis()-startTime)+"  毫秒");
        }
    }

    @Test
    public void generateEncodePasswordTest(){
        String pwd = "asdqwe123";
        String encode = passwordEncoder.encode(pwd);
        System.out.println(encode);
        String token = "$2a$12$KquPKNWpgySeIOZhLhGHSuJBTKRQNoSYQ/L3.t3x9KUqKoIkj5oWS";
        Assert.assertTrue(passwordEncoder.matches(pwd,token));
    }
}
