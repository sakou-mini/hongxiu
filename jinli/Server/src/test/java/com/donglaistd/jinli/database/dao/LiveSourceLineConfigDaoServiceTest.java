package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.statistic.LiveSourceLineConfigDaoService;
import com.donglaistd.jinli.database.entity.system.LiveSourceLineConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class LiveSourceLineConfigDaoServiceTest extends BaseTest {
    @Autowired
    LiveSourceLineConfigDaoService liveSourceLineConfigDaoService;

    @Test
    public void findLiveSourceLineTest(){
        LiveSourceLineConfig liveSourceLineConfig = liveSourceLineConfigDaoService.findLiveSourceLineConfigByPlatformType(Constant.PlatformType.PLATFORM_JINLI);
        Assert.assertEquals(5, liveSourceLineConfig.getAvailableLine().size());
        liveSourceLineConfig.setAvailableLine(Sets.newHashSet(Constant.LiveSourceLine.TENCENT_LINE, Constant.LiveSourceLine.WANGSU_LINE));
        liveSourceLineConfigDaoService.save(liveSourceLineConfig);
        liveSourceLineConfig = liveSourceLineConfigDaoService.findLiveSourceLineConfigByPlatformType(Constant.PlatformType.PLATFORM_JINLI);
        Assert.assertEquals(2, liveSourceLineConfig.getAvailableLine().size());
    }
}
