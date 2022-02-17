package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.database.dao.platform.PlatformRechargeRecordDaoService;
import com.donglaistd.jinli.database.entity.plant.PlatformRechargeRecord;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PlatformRechargeRecordDaoServiceTest extends BaseTest {
    @Autowired
    PlatformRechargeRecordDaoService platformRechargeRecordDaoService;

    @Test
    public void totalRechargeAmountTest(){
        PlatformRechargeRecord record1 = PlatformRechargeRecord.newInstance("123", "333", "xxx", 20, 200,0,0);
        PlatformRechargeRecord record2 = PlatformRechargeRecord.newInstance("123", "333", "xxx", 30, 300,0,0);
        PlatformRechargeRecord record3 = PlatformRechargeRecord.newInstance("123", "333", "xxx", 20, 200,0,0);
        platformRechargeRecordDaoService.save(record1);
        platformRechargeRecordDaoService.save(record2);
        platformRechargeRecordDaoService.save(record3);
        long amount = platformRechargeRecordDaoService.totalRechargeCoinByUserId("123");
        Assert.assertEquals(700,amount);
    }
}
