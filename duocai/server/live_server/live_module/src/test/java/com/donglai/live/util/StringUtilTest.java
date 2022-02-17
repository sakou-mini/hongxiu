package com.donglai.live.util;

import com.donglai.common.constant.PathConstant;
import com.donglai.common.util.HttpUtil;
import com.donglai.live.BaseTest;
import org.junit.Test;

public class StringUtilTest extends BaseTest {

    @Test
    public void domainCheckTest(){
        boolean b = HttpUtil.verifyHostIsAvailable("http://static.365mitu.com" + PathConstant.H5_DOMAIN_TEST_PATH);
        System.out.println(b);
    }
}
