package com.donglaistd.jinli.util;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.metadata.Metadata;
import org.junit.Assert;
import org.junit.Test;

public class MetaUtilTest extends BaseTest {
    @Test
    public void getVipLevelByLevelTest(){
        Metadata.VipDefine vipConfig = MetaUtil.getVipDefineByPlayerLevel(5);
        Assert.assertEquals(Metadata.VipEmum.vipEmum0,vipConfig.getVipId());

        vipConfig = MetaUtil.getVipDefineByPlayerLevel(10);
        Assert.assertEquals(Metadata.VipEmum.vipEmum1,vipConfig.getVipId());

        vipConfig = MetaUtil.getVipDefineByPlayerLevel(19);
        Assert.assertEquals(Metadata.VipEmum.vipEmum1,vipConfig.getVipId());

        vipConfig = MetaUtil.getVipDefineByPlayerLevel(20);
        Assert.assertEquals(Metadata.VipEmum.vipEmum2,vipConfig.getVipId());

        vipConfig = MetaUtil.getVipDefineByPlayerLevel(30);
        Assert.assertEquals(Metadata.VipEmum.vipEmum2,vipConfig.getVipId());

        vipConfig = MetaUtil.getVipDefineByPlayerLevel(50);
        Assert.assertEquals(Metadata.VipEmum.vipEmum3,vipConfig.getVipId());

        vipConfig = MetaUtil.getVipDefineByPlayerLevel(80);
        Assert.assertEquals(Metadata.VipEmum.vipEmum4,vipConfig.getVipId());

        vipConfig = MetaUtil.getVipDefineByPlayerLevel(100);
        Assert.assertEquals(Metadata.VipEmum.vipEmum5,vipConfig.getVipId());

        vipConfig = MetaUtil.getVipDefineByPlayerLevel(150);
        Assert.assertEquals(Metadata.VipEmum.vipEmum6,vipConfig.getVipId());

        vipConfig = MetaUtil.getVipDefineByPlayerLevel(250);
        Assert.assertEquals(Metadata.VipEmum.vipEmum6,vipConfig.getVipId());
    }

    @Test
    public void getPlayerLevelExpByLevel(){
        long expSum = 0;
        for (int i = 1; i <= 50; i++) {
            Metadata.PlayerDefine playerDefine = MetaUtil.getPlayerDefineByCurrentLevel(i);
            expSum += playerDefine.getExp();
        }
        System.out.println("所需总经验:"+expSum);
    }
}
