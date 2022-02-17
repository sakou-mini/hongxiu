package com.donglaistd.jinli.database.dao.statistic;

import com.donglaistd.jinli.database.dao.FollowListDaoService;
import com.donglaistd.jinli.database.dao.LiveRecordDaoService;
import com.donglaistd.jinli.database.entity.LiveRecord;
import com.donglaistd.jinli.database.entity.statistic.DailyLiveRecordInfo;
import com.donglaistd.jinli.domain.LiveRecordResult;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class DailyLiveRecordInfoDaoService {
    @Autowired
    DailyLiveRecordInfoRepository dailyLiveRecordInfoRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    LiveRecordDaoService liveRecordDaoService;
    @Autowired
    FollowListDaoService followListDaoService;

    @Value("${live.attendance.time}")
    private long liveAttendanceTime;


    public List<DailyLiveRecordInfo> saveAll(List<DailyLiveRecordInfo> recordInfos) {
        return dailyLiveRecordInfoRepository.saveAll(recordInfos);
    }

    public DailyLiveRecordInfo save(DailyLiveRecordInfo dailyLiveRecordInfo) {
        return dailyLiveRecordInfoRepository.save(dailyLiveRecordInfo);
    }

    public List<DailyLiveRecordInfo> findByLiveUserIdAndTimes(String liveUserId, long startTime, long endTime) {
        Criteria criteria = Criteria.where("liveUserId").is(liveUserId).and("time").gte(startTime).lte(endTime);
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.ASC, "time"));
        return mongoTemplate.find(query, DailyLiveRecordInfo.class);
    }

    public List<DailyLiveRecordInfo> totalDailyLiveInfo(long startTime, long endTime) {
        List<LiveRecordResult> liveRecords = liveRecordDaoService.getTotalGroupLiveRecordByTimes(startTime, endTime);
        Map<String, Integer> fansMap = followListDaoService.countLiveUserFansNumByTimes(startTime, endTime);
        List<DailyLiveRecordInfo> dailyLiveRecordInfos = new ArrayList<>(liveRecords.size());
        DailyLiveRecordInfo dailyLiveRecordInfo;
        int fansNum;
        int rank = 1;
        for (LiveRecordResult liveRecord : liveRecords) {
            fansNum = fansMap.getOrDefault(liveRecord.getLiveUserId(), 0);
            dailyLiveRecordInfo = DailyLiveRecordInfo.newInstance(liveRecord.getLiveUserId(),
                    liveRecord.getLiveTime(), liveRecord.getGiftFlow(), rank, liveRecord.getAudienceNum(), startTime, fansNum, liveRecord.getPlatform());
            dailyLiveRecordInfos.add(dailyLiveRecordInfo);
            rank ++;
        }
        return dailyLiveRecordInfos;
    }

    public DailyLiveRecordInfo totalDailyLiveInfoBuLiveUser(String liverUserId, long startTime, long endTime) {
        LiveRecord liveRecord = liveRecordDaoService.totalLiveRecordByLiveUserIdAndTimes(liverUserId, startTime, endTime);
        if (Objects.isNull(liveRecord)) return new DailyLiveRecordInfo();
        return DailyLiveRecordInfo.newInstance(liveRecord, TimeUtil.getCurrentDayStartTime(), 0, 0);
    }

    public long countLiveNumByTimeBetween(long startTime, long endTime) {
        return mongoTemplate.count(Query.query(Criteria.where("time").gte(startTime).lte(endTime)), DailyLiveRecordInfo.class);
    }

    public PageInfo<DailyLiveRecordInfo> findPageDailyLiveRecordByTimeBetweenAndLiveTime(long startTime, long endTime, boolean isStandards,int page, int size) {
        Pageable pageRequest = PageRequest.of(page, size);
        Criteria criteria = Criteria.where("time").gte(startTime).lte(endTime);
        criteria = isStandards ? criteria.and("liveTime").gte(liveAttendanceTime) : criteria.and("liveTime").lte(liveAttendanceTime);
        Query query = Query.query(criteria);
        long totalNum = mongoTemplate.count(query, DailyLiveRecordInfo.class);
        List<DailyLiveRecordInfo> content = mongoTemplate.find(query.with(pageRequest).with(Sort.by(Sort.Direction.ASC,"time")), DailyLiveRecordInfo.class);
        return new PageInfo<>(content, pageRequest, totalNum);
    }
}
