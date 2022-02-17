package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.rank.GiftLog;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.util.TimeUtil;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GiftLogDaoServiceTest extends BaseTest {
    @Autowired
    private GiftLogDaoService giftLogDaoService;
    @Test
    public void testSave() {
        GiftLog giftLog = giftLogDaoService.save(GiftLog.newInstance(user.getId(), ObjectId.get().toString(), 20,"10008",1));
        List<GiftLog> list = giftLogDaoService.findBySenderId(user.getId());
        Assert.assertFalse(list.isEmpty());
    }

    @Test
    public void testTimeUtil() {
        long now = System.currentTimeMillis();
        long endTime = TimeUtil.todayStartTime(now);
        long weekTime = TimeUtil.mondayStartTime(endTime);
        System.err.println(endTime);
        System.err.println(weekTime);
    }

    @Test
    public void testFindByCreateTimeBetweenAndGroupBySenderId() {
        long endTime = TimeUtil.todayStartTime(System.currentTimeMillis());
        long startTime = TimeUtil.yesterdayStartTime(endTime);
        String receiveId = ObjectId.get().toString();
        String senderId1 = ObjectId.get().toString();
        String senderId2 = ObjectId.get().toString();
        GiftLog log1 = giftLogDaoService.save(GiftLog.newInstance(user.getId(),receiveId , 20,"10008",1));
        log1.setCreateTime(endTime-1000*60*60);
        GiftLog log2 = giftLogDaoService.save(GiftLog.newInstance(user.getId(),receiveId , 30,"10008",1));
        log2.setCreateTime(endTime-1000*60*60*2);
        GiftLog log3 = giftLogDaoService.save(GiftLog.newInstance(senderId1,receiveId , 100,"10008",1));
        log3.setCreateTime(endTime-1000*60*60*3);
        GiftLog log4 = giftLogDaoService.save(GiftLog.newInstance(senderId1,receiveId , 50,"10008",1));
        log4.setCreateTime(endTime-1000*60*60*4);
        GiftLog log5 = giftLogDaoService.save(GiftLog.newInstance(senderId2,receiveId , 10,"10008",1));
        log5.setCreateTime(endTime-1000*60*60*5);
        GiftLog log6 = giftLogDaoService.save(GiftLog.newInstance(senderId2,receiveId , 5,"10008",1));
        log6.setCreateTime(endTime-1000*60*60*6);
        List<GiftLog> logs = giftLogDaoService.saveAll(Arrays.asList(log1, log2, log3, log4, log5, log6));
        Assert.assertEquals(6,logs.size());
        List<GiftLog> group = giftLogDaoService.findByCreateTimeBetweenAndGroupBySenderId(10, startTime, endTime);
        Assert.assertEquals(3,group.size());
        Assert.assertEquals(senderId1,group.get(0).getSenderId());
        group.stream().forEach(System.err::println);
    }
    @Test
    public void testFindByCreateTimeBetweenAndGroupByReceiveId() {
        long endTime = TimeUtil.todayStartTime(System.currentTimeMillis());
        long startTime = TimeUtil.yesterdayStartTime(endTime);
        String receiveId = ObjectId.get().toString();
        String senderId1 = ObjectId.get().toString();
        String senderId2 = ObjectId.get().toString();
        GiftLog log1 = giftLogDaoService.save(GiftLog.newInstance(user.getId(),receiveId , 20,"10008",1));
        log1.setCreateTime(endTime-1000*60*60);
        GiftLog log2 = giftLogDaoService.save(GiftLog.newInstance(user.getId(),receiveId , 30,"10008",1));
        log2.setCreateTime(endTime-1000*60*60*2);
        GiftLog log3 = giftLogDaoService.save(GiftLog.newInstance(senderId1,receiveId , 100,"10008",1));
        log3.setCreateTime(endTime-1000*60*60*3);
        GiftLog log4 = giftLogDaoService.save(GiftLog.newInstance(senderId1,receiveId , 50,"10008",1));
        log4.setCreateTime(endTime-1000*60*60*4);
        GiftLog log5 = giftLogDaoService.save(GiftLog.newInstance(senderId2,receiveId , 10,"10008",1));
        log5.setCreateTime(endTime-1000*60*60*5);
        GiftLog log6 = giftLogDaoService.save(GiftLog.newInstance(senderId2,receiveId , 5,"10008",1));
        log6.setCreateTime(endTime-1000*60*60*6);
        List<GiftLog> logs = giftLogDaoService.saveAll(Arrays.asList(log1, log2, log3, log4, log5, log6));
        Assert.assertEquals(6,logs.size());
        List<GiftLog> group = giftLogDaoService.findByCreateTimeBetweenAndGroupByReceiveId(10, startTime, endTime);
        Assert.assertEquals(1, group.size());
        Assert.assertEquals(receiveId,group.get(0).getReceiveId());
    }
    @Test
    public void testFindByLiveUserIdAndGroupSenderId() {
        long endTime = TimeUtil.todayStartTime(System.currentTimeMillis());
        long startTime = TimeUtil.yesterdayStartTime(endTime);
        String receiveId = "123";
        String senderId1 = "afasf";
        String senderId2 = "123214";
        GiftLog log1 = giftLogDaoService.save(GiftLog.newInstance(user.getId(),receiveId , 20,"10008",1));
        log1.setCreateTime(endTime-1000*60*60);
        GiftLog log2 = giftLogDaoService.save(GiftLog.newInstance(user.getId(),receiveId , 30,"10008",1));
        log2.setCreateTime(endTime-1000*60*60*2);
        GiftLog log3 = giftLogDaoService.save(GiftLog.newInstance(senderId1,receiveId , 100,"10008",1));
        log3.setCreateTime(endTime-1000*60*60*3);
        GiftLog log4 = giftLogDaoService.save(GiftLog.newInstance(senderId1,receiveId , 50,"10008",1));
        log4.setCreateTime(endTime-1000*60*60*4);
        GiftLog log5 = giftLogDaoService.save(GiftLog.newInstance(senderId2,receiveId , 10,"10008",1));
        log5.setCreateTime(endTime-1000*60*60*5);
        GiftLog log6 = giftLogDaoService.save(GiftLog.newInstance(senderId2,receiveId , 5,"10008",1));
        log6.setCreateTime(endTime-1000*60*60*6);
        List<GiftLog> logs = giftLogDaoService.saveAll(Arrays.asList(log1, log2, log3, log4, log5, log6));
        Assert.assertEquals(6,logs.size());
        List<GiftLog> group = giftLogDaoService.findByLiveUserIdAndGroupSenderId(receiveId, 10, startTime, endTime);
        group.stream().forEach(System.err::println);
    }

    @Test
    public void testTotalGiftSendAmountAndSederNum(){
        List<GiftLog> giftLogList = new ArrayList<>();
        giftLogList.add(GiftLog.newInstance("yty1", "rec", 10,"10008",1));
        giftLogList.add(GiftLog.newInstance("yty1", "rec", 10,"10008",1));
        giftLogList.add(GiftLog.newInstance("yty3", "rec2", 10,"10008",1));
        giftLogDaoService.saveAll(giftLogList);
        GiftLog giftInfo = giftLogDaoService.totalGiftUserNumAndGiftAmountByTimeBetween(0, System.currentTimeMillis());
        Assert.assertEquals(30,giftInfo.getSendAmount());
        Assert.assertEquals(2,giftInfo.getCreateTime());
    }

    @Test
    public void totalUserGiftInfoByTimeBetweenAndGroupTest2(){
        List<GiftLog> giftLogList = new ArrayList<>();
        giftLogList.add(GiftLog.newInstance("yty1", "rec", 10,"10008",1));
        giftLogList.add(GiftLog.newInstance("yty1", "rec", 20,"10008",2));
        giftLogList.add(GiftLog.newInstance("yty1", "re2", 20,"10018",10));
        giftLogList.add(GiftLog.newInstance("yty1", "rec", 20,"10019",1));
        giftLogList.add(GiftLog.newInstance("yty3", "rec", 10,"10008",1));
        giftLogList.add(GiftLog.newInstance("yty2", "rec2", 10,"10008",1));
        giftLogDaoService.saveAll(giftLogList);
        PageInfo<GiftLog> giftLogs = giftLogDaoService.totalPageUserGiftInfoByTimeBetween(TimeUtil.getCurrentDayStartTime(), System.currentTimeMillis(), 1, 3);
        Assert.assertEquals(3,giftLogs.getContent().size());
        PageInfo<GiftLog> giftLogs2 = giftLogDaoService.totalPageUserGiftInfoByTimeBetween(TimeUtil.getCurrentDayStartTime(), System.currentTimeMillis(), 2, 3);
        Assert.assertEquals(2,giftLogs2.getContent().size());
        long totalNum = giftLogs.getTotal();
        Assert.assertEquals(5,totalNum);
    }

    @Test
    public void findGiftLogByTimesAndSendUserIdAndNameTest(){
        User user1 = createTester(3000, "送礼者1");
        User user2 = createTester(3000, "接收者");
        LiveUser liveUser1 = liveUserBuilder.create(user2.getId(), Constant.LiveStatus.ONLINE,Constant.PlatformType.PLATFORM_JINLI);
        Room room1 = roomBuilder.create(liveUser1.getId(), liveUser1.getUserId(),"", "","");
        liveUser1.setRoomId(room1.getId());
        user2.setLiveUserId(liveUser1.getId());
        userDaoService.save(user2);
        List<GiftLog> giftLogList = new ArrayList<>();
        giftLogList.add(GiftLog.newInstance(user1.getId(), user2.getId(), 10,"10008",10));
        giftLogList.add(GiftLog.newInstance(user1.getId(), user2.getId(), 10,"10010",5));
        giftLogList.add(GiftLog.newInstance(user1.getId(), user.getId(), 10,"10010",5));
        giftLogDaoService.saveAll(giftLogList);
        PageInfo<GiftLog> pageInfo = giftLogDaoService.findGiftLogByTimesAndSendUserIdAndName(TimeUtil.getCurrentDayStartTime(),
                System.currentTimeMillis(), user1.getId(), "接收者",  room1.getDisplayId(), PageRequest.of(0,3), Constant.PlatformType.PLATFORM_JINLI);
        Assert.assertEquals(2,pageInfo.getTotal());
        Assert.assertEquals(2,pageInfo.getContent().size());

        pageInfo = giftLogDaoService.findGiftLogByTimesAndSendUserIdAndName(TimeUtil.getCurrentDayStartTime(),
                System.currentTimeMillis(), "", "",  room.getDisplayId(),PageRequest.of(0,3),Constant.PlatformType.PLATFORM_JINLI);
        Assert.assertEquals(1,pageInfo.getTotal());
        Assert.assertEquals(1,pageInfo.getContent().size());
    }
}
