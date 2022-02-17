package com.donglaistd.jinli.util;

import com.donglaistd.jinli.Constant;
import org.junit.Assert;
import org.junit.Test;

import static com.donglaistd.jinli.util.StringUtils.checkAccountName;
import static com.donglaistd.jinli.util.StringUtils.generateTouristName;

public class StringUtilsTest {
    @Test
    public void TestGenerateTouristName() {
        Assert.assertEquals("QMYT26", generateTouristName(0));
        Assert.assertEquals("IFT8PY", generateTouristName(1));
        Assert.assertEquals("AY5IRA", generateTouristName(100000));
        Assert.assertEquals("VGKNUT", generateTouristName(2176782335L));
        Assert.assertEquals("QMYT26", generateTouristName(2176782336L));
    }

    @Test
    public void TestAccountNameChecking() {
        Assert.assertTrue(checkAccountName("abcd"));
        Assert.assertTrue(checkAccountName("abcdefghijklmnop"));
        Assert.assertTrue(checkAccountName("a___"));
        Assert.assertTrue(checkAccountName("ZYXW"));

        Assert.assertFalse(checkAccountName("abc"));
        Assert.assertFalse(checkAccountName("abcdefghijklmnopo"));
        Assert.assertFalse(checkAccountName("!@#$%^"));
        Assert.assertFalse(checkAccountName("a__h "));
        Assert.assertFalse(checkAccountName("汉字测试"));
        Assert.assertFalse(checkAccountName("1abc"));
    }


    @Test
    public void checkDomainTest(){
        String domain = "ws.jinli88.net";
        String rootDomain = StringUtils.getRootDomain(domain);
        Assert.assertEquals("jinli88.net", rootDomain);
        domain = "ws.23jinli88.vip";
        rootDomain = StringUtils.getRootDomain(domain);
        Assert.assertEquals("23jinli88.vip", rootDomain);
    }

    @Test
    public void test(){
        Constant.LiveUserPermission permission_gift = Constant.LiveUserPermission.valueOf("PERMISSION_GIFT1");
    }
}
