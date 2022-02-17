package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.LiveRecord;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.util.TimeUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class LiveRecordDaoTest extends BaseTest {
    @Autowired
    LiveRecordDaoService liveRecordDaoService;

    @Test
    public void test(){
        long startTime = System.currentTimeMillis()-2000;
        long endTime = System.currentTimeMillis();
        LiveRecord liveRecord = LiveRecord.newInstance("111", "2122", "123124", startTime, 2000,
                1000, 100, 0, Constant.GameType.JIAOYOU, Constant.PlatformType.PLATFORM_JINLI);
        LiveRecord liveRecord2 = LiveRecord.newInstance("111", "2122", "123124", startTime - 4000, 4000,
                1000, 100, 0, Constant.GameType.JIAOYOU, Constant.PlatformType.PLATFORM_JINLI);
        liveRecordDaoService.save(liveRecord);
        liveRecordDaoService.save(liveRecord2);
        PageInfo<LiveRecord> liveRecordPageInfo = liveRecordDaoService.pageQueryLiveRecord(10, 0, "", "", Constant.PlatformType.PLATFORM_JINLI, TimeUtil.getCurrentDayStartTime(), System.currentTimeMillis());
    }
}
