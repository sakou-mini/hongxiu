package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.statistic.DailyUserActiveRecordDaoService;
import com.donglaistd.jinli.database.entity.LiveWatchRecord;
import com.donglaistd.jinli.database.entity.statistic.DailyUserActiveRecord;
import com.donglaistd.jinli.database.entity.statistic.record.UserRoomRecord;
import com.donglaistd.jinli.domain.LiveWatchRecordResult;
import com.donglaistd.jinli.http.dto.request.UserListRequest;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.service.statistic.CommonStatisticProcess;
import com.donglaistd.jinli.service.statistic.UserManagerPageProcess;
import com.donglaistd.jinli.util.TimeUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.donglaistd.jinli.Constant.PlatformType.PLATFORM_JINLI_VALUE;

public class LiveWatchRecordDaoServiceTest extends BaseTest {

    @Autowired
    LiveWatchRecordDaoService liveWatchRecordDaoService;
    @Autowired
    CommonStatisticProcess commonStatisticProcess;
    @Autowired
    UserManagerPageProcess userManagerPageProcess;

    @Test
    public void totalWatchLiveRecordTest(){
        List<LiveWatchRecord> records = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            UserRoomRecord userRoomRecord = new UserRoomRecord(System.currentTimeMillis() - (i * 1000), room.getId(), user.getId());
            userRoomRecord.addBulletMessageCount();
            userRoomRecord.addConnectedLiveCount();
            userRoomRecord.addGiftCoin(5);
            LiveWatchRecord liveWatchRecord = new LiveWatchRecord(userRoomRecord, System.currentTimeMillis(), Constant.MobilePhoneBrand.BRAND_OTHER, user.getPlatformType(), room.getId(), "127.0.0.1", room.getLiveUserId());
            records.add(liveWatchRecord);
        }
        liveWatchRecordDaoService.saveAll(records);

        List<LiveWatchRecordResult> liveWatchRecordResults = liveWatchRecordDaoService.totalAllUserWatchRecordByTimes(TimeUtil.getCurrentDayStartTime(), System.currentTimeMillis());
        Assert.assertEquals(1,liveWatchRecordResults.size());
        LiveWatchRecordResult liveWatchRecordResult = liveWatchRecordResults.get(0);
        Assert.assertEquals(1000, liveWatchRecordResult.getBulletMessageCount());
        Assert.assertEquals(1000, liveWatchRecordResult.getConnectedLiveCount());
        Assert.assertEquals(5000, liveWatchRecordResult.getGiftFlow());
        Assert.assertEquals(1000, liveWatchRecordResult.getGiftCount());

        records.clear();
        for (int i = 0; i < 1000; i++) {
            UserRoomRecord userRoomRecord = new UserRoomRecord(System.currentTimeMillis() - (i * 1000), room.getId(), "用户id1");
            LiveWatchRecord liveWatchRecord = new LiveWatchRecord(userRoomRecord, System.currentTimeMillis(), Constant.MobilePhoneBrand.BRAND_OTHER, user.getPlatformType(), room.getId(), "127.0.0.1", room.getLiveUserId());
            records.add(liveWatchRecord);
        }
        liveWatchRecordDaoService.saveAll(records);

        commonStatisticProcess.totalAllUserActiveData(TimeUtil.getCurrentDayStartTime(),TimeUtil.getDayEndTime(System.currentTimeMillis()));

        UserListRequest userListRequest = new UserListRequest();
        userListRequest.setPlatform(PLATFORM_JINLI_VALUE);
        userListRequest.setStartTime(TimeUtil.getCurrentDayStartTime());
        userListRequest.setEndTime(TimeUtil.getDayEndTime(System.currentTimeMillis()));
        userListRequest.setPage(0);
        userListRequest.setSize(1);
        PageInfo<DailyUserActiveRecord> pageInfo = userManagerPageProcess.queryDailyUserActive(userListRequest);
        Assert.assertEquals(2, pageInfo.getTotal());
        Assert.assertEquals(1,pageInfo.getContent().size());
    }
}
