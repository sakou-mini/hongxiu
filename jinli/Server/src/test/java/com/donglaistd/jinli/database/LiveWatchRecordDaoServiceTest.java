package com.donglaistd.jinli.database;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.database.dao.LiveWatchRecordDaoService;
import com.donglaistd.jinli.database.entity.LiveWatchRecord;
import com.donglaistd.jinli.domain.LiveWatchRecordResult;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class LiveWatchRecordDaoServiceTest extends BaseTest {
    @Autowired
    LiveWatchRecordDaoService liveWatchRecordDaoService;
    @Test
    public void totalWatchLiveRecordTest() {
        LiveWatchRecord liveWatchRecord;
        for (int i = 0; i < 3; i++) {
            liveWatchRecord = new LiveWatchRecord();
            liveWatchRecord.setUserId("123");
            liveWatchRecord.setRoomId("roomId");
            liveWatchRecord.setWatchTime(1000);
            liveWatchRecord.setBulletMessageCount(5);
            liveWatchRecord.setConnectedLiveCount(2);
            liveWatchRecordDaoService.save(liveWatchRecord);
        }
        List<LiveWatchRecord> liveWatchRecords = liveWatchRecordDaoService.groupUserWatchRecord();
        Assert.assertEquals(1, liveWatchRecords.size());
        LiveWatchRecord liveWatchRecord1 = liveWatchRecords.get(0);
        Assert.assertEquals(3000, liveWatchRecord1.getWatchTime());
        Assert.assertEquals(3, liveWatchRecord1.getConnectedLiveCount());
        Assert.assertEquals(1, liveWatchRecord1.getBulletMessageCount());

        LiveWatchRecordResult liveWatchRecordResult = liveWatchRecordDaoService.totalUserWatchRecord("1233");
        Assert.assertEquals(3000, liveWatchRecordResult.getWatchTime());
        Assert.assertEquals(1, liveWatchRecordResult.getWatchNum());
        Assert.assertEquals(3, liveWatchRecordResult.getWatchCount());
        Assert.assertEquals(6, liveWatchRecordResult.getConnectedLiveCount());
        Assert.assertEquals(15, liveWatchRecordResult.getBulletMessageCount());
        Assert.assertEquals("123", liveWatchRecordResult.getUserId());

    }
}
