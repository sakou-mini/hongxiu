package com.donglaistd.jinli.other;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.metadata.Metadata;
import com.donglaistd.jinli.util.MetaUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MetaUtilTest extends BaseTest {
    @Test
    public void testVip() {
        Metadata.VipDefine vipDefine = MetaUtil.getVipDefineByCurrentLevel(Constant.VipType.LOCKED_VALUE);
        Assert.assertEquals(Constant.VipType.LOCKED.getNumber(), vipDefine.getVipIdValue());
        Assert.assertEquals(Constant.VipType.RANGER_VALUE, vipDefine.getNextLvl());
    }

    @Test
    public void testUpdateVip() {
        user.setDisplayName("test8848");
        user.setAccountName("test8848_AccountName");
        user.setToken("test8848_token");
        user.setLevel(9);
        boolean update = user.updateVipByLevel();
        Assert.assertFalse(update);
        Assert.assertEquals(Constant.VipType.LOCKED,user.getVipType());
        user.setLevel(10);
        update = user.updateVipByLevel();
        Assert.assertTrue(update);
        Assert.assertEquals(Constant.VipType.RANGER,user.getVipType());
    }

    @Test
    public void testUpdateVipFail() {
        user.setDisplayName("test8848");
        user.setAccountName("test8848_AccountName");
        user.setToken("test8848_token");
        user.setLevel(5);
        boolean update = user.updateVipByLevel();
        Assert.assertFalse(update);
        user.setLevel(9);
        update = user.updateVipByLevel();
        Assert.assertFalse(update);
        //Assert.assertEquals(110,user.getSendAmount());
    }
}
