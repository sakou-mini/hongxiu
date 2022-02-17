package com.donglaistd.jinli.util;

import cn.hutool.core.util.RandomUtil;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.SpringContext;
import com.donglaistd.jinli.database.dao.*;
import com.donglaistd.jinli.database.dao.statistic.DailyUserActiveRecordDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeUser;
import com.donglaistd.jinli.database.entity.backoffice.PersonDiaryOperationlog;
import com.donglaistd.jinli.database.entity.statistic.DailyUserActiveRecord;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import com.donglaistd.jinli.database.entity.zone.RecommendDiary;
import com.donglaistd.jinli.domain.LiveWatchRecordResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.donglaistd.jinli.Constant.DiaryStatue.*;
import static com.donglaistd.jinli.constant.BackOfficeConstant.ROOT_ACCOUNT;

public class MockDataUtil {

    public static List<Jinli.RankInfo> mockGiftRackData(int num){
        List<Jinli.RankInfo> rankInfos = new ArrayList<>(num);
        Jinli.RankInfo.Builder builder;
        for (int i = 0; i < num; i++) {
            builder = Jinli.RankInfo.newBuilder();
            builder.setUserId("70000" + i)
                    .setLevel(2+i)
                    .setAvatarUrl(DefaultImageUtil.getUserDefaultByNumber(i))
                    .setDisplayName("游客_"+StringUtils.generateTouristName(Integer.parseInt(builder.getUserId())))
                    .setVipType(Constant.VipType.RANGER)
                    .setAmount(20+i*10)
                    .setLiveStatus(Constant.LiveStatus.OFFLINE)
                    .build();
            rankInfos.add(builder.build());
        }
        return rankInfos;
    }

    public static List<Jinli.RankInfo> mockContributeRankData(int num){
        List<Jinli.RankInfo> rankInfos = new ArrayList<>(num);
        Jinli.RankInfo.Builder builder;
        for (int i = 0; i < num; i++) {
            builder = Jinli.RankInfo.newBuilder();
            builder.setUserId("60000" + i)
                    .setLevel(5+i)
                    .setAvatarUrl(DefaultImageUtil.getUserDefaultByNumber(num-i))
                    .setDisplayName("游客_"+StringUtils.generateTouristName(Integer.parseInt(builder.getUserId())))
                    .setVipType(Constant.VipType.RANGER)
                    .setAmount(50+i*10)
                    .setLiveStatus(Constant.LiveStatus.OFFLINE)
                    .build();
            rankInfos.add(builder.build());
        }
        return rankInfos;
    }

    public static List<Jinli.RankInfo> mockFansRankData(int num){
        List<Jinli.RankInfo> rankInfos = new ArrayList<>(num);
        Jinli.RankInfo.Builder builder;
        for (int i = 0; i < num; i++) {
            builder = Jinli.RankInfo.newBuilder();
            builder.setUserId("50000" + i)
                    .setLevel(5+i)
                    .setAvatarUrl(DefaultImageUtil.getUserDefaultByNumber(i))
                    .setDisplayName("游客_"+StringUtils.generateTouristName(Integer.parseInt(builder.getUserId())))
                    .setVipType(Constant.VipType.RANGER)
                    .setAmount(20+i*10)
                    .setLiveStatus(Constant.LiveStatus.OFFLINE)
                    .build();
            rankInfos.add(builder.build());
        }
        return rankInfos;
    }

    //TODO DELETE
    public static void mockDailyUserActiveRecord(){
        UserDaoService userDaoService = SpringContext.getBean(UserDaoService.class);
        DailyUserActiveRecordDaoService dailyUserActiveRecordDaoService = SpringContext.getBean(DailyUserActiveRecordDaoService.class);
        if(!dailyUserActiveRecordDaoService.findAll().isEmpty()) return;
        List<DailyUserActiveRecord> records = new ArrayList<>();
        for (User user : userDaoService.findAll()) {
            LiveWatchRecordResult liveWatchRecordResult = new LiveWatchRecordResult(10, 5, cn.hutool.core.util.RandomUtil.randomInt(5000, 90000), 1, 2, user.getId(), 1, RandomUtil.randomInt(1, 10), user.getPlatformType());
            records.add(DailyUserActiveRecord.newInstance(liveWatchRecordResult, TimeUtil.getCurrentDayStartTime()));
            records.add(DailyUserActiveRecord.newInstance(liveWatchRecordResult, TimeUtil.getBeforeDayStartTime(2)));
        }
        dailyUserActiveRecordDaoService.saveAll(records);
    }

    public static void mockPersonDiary(){
        PersonDiaryDaoService personDiaryDaoService = SpringContext.getBean(PersonDiaryDaoService.class);
        if(!personDiaryDaoService.findDiaryByStatue(DIARY_APPROVAL_PASS).isEmpty()) return;
        BackOfficeUserDaoService backOfficeUserDaoService = SpringContext.getBean(BackOfficeUserDaoService.class);
        LiveUserDaoService liveUserDaoService = SpringContext.getBean(LiveUserDaoService.class);
        PersonDiaryOperationLogDaoService personDiaryOperationLogDaoService = SpringContext.getBean(PersonDiaryOperationLogDaoService.class);
        RecommendDiaryDaoService recommendDiaryDaoService = SpringContext.getBean(RecommendDiaryDaoService.class);
        BackOfficeUser backOfficeUser = backOfficeUserDaoService.findByAccountName(ROOT_ACCOUNT);
        int recommend = 1;
        for (LiveUser liveUser : liveUserDaoService.findAllPassLiveUser()) {
            PersonDiary diary;
            for (int i = 0; i < 10; i++) {
                diary = PersonDiary.newInstance(liveUser.getUserId(), "审核通过"+i, Constant.DiaryType.IMAGE,9);
                diary.setStatue(DIARY_APPROVAL_PASS);
                PersonDiaryOperationlog personDiaryOperationlog = new PersonDiaryOperationlog();
                personDiaryOperationlog.setBackOfficeId(backOfficeUser.getId());
                personDiaryOperationlog.setOperationDate(new Date());
                personDiaryOperationlog.setApproval(true);
                personDiaryOperationlog.setPersonDiaryId(diary.getId());
                personDiaryDaoService.save(diary);
                personDiaryOperationLogDaoService.save(personDiaryOperationlog);
                if(i >=0 && i<3){
                    RecommendDiary recommendDiary = RecommendDiary.newInstance(diary.getId(), recommend, Constant.DiaryRecommendTimeType.RECOMMEND_SIX_HOUR);
                    recommendDiaryDaoService.save(recommendDiary);
                    recommend++;
                }
            }


            for (int i = 0; i < 6; i++) {
                diary = PersonDiary.newInstance(liveUser.getUserId(), "审核不通过"+i, Constant.DiaryType.IMAGE,9);
                diary.setStatue(DIARY_APPROVED_FAIL);
                PersonDiaryOperationlog personDiaryOperationlog = new PersonDiaryOperationlog();
                personDiaryOperationlog.setBackOfficeId(backOfficeUser.getId());
                personDiaryOperationlog.setOperationDate(new Date());
                personDiaryOperationlog.setApproval(false);
                personDiaryOperationlog.setPersonDiaryId(diary.getId());
                personDiaryDaoService.save(diary);
                personDiaryOperationLogDaoService.save(personDiaryOperationlog);
            }

            for (int i = 0; i < 5; i++) {
                diary = PersonDiary.newInstance(liveUser.getUserId(), "未审核"+i, Constant.DiaryType.IMAGE,9);
                diary.setStatue(DIARY_UNAPPROVED);
                personDiaryDaoService.save(diary);
            }
            // 10 个 审核通过且 3 个被推荐  ，6个 审核不通过 ，5个未审核
        }
    }
}
