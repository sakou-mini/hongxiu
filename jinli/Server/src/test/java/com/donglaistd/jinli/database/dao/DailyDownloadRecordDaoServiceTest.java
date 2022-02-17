package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.database.dao.statistic.DailyDownloadRecordDaoService;
import com.donglaistd.jinli.database.entity.statistic.DailyDownloadRecord;
import com.donglaistd.jinli.util.ScheduledTaskUtil;
import com.donglaistd.jinli.util.TimeUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DailyDownloadRecordDaoServiceTest extends BaseTest {
    @Autowired
    DailyDownloadRecordDaoService dailyDownloadRecordDaoService;

    @Test
    public void threadAddDownloadCountTest() throws InterruptedException {
        for (int i = 0; i <10 ; i++) {
            ScheduledTaskUtil.schedule(() -> dailyDownloadRecordDaoService.addDownloadCountByToday(), 0);
        }
        Thread.sleep(2000);
        DailyDownloadRecord dailyDownloadRecord = dailyDownloadRecordDaoService.finByTime(TimeUtil.getCurrentDayStartTime());
        Assert.assertEquals(10,dailyDownloadRecord.getDownloadCount());

    }
}
