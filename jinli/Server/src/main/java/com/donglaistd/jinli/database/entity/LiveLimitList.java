package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.http.dto.request.LiveTimeImportRequest;
import com.donglaistd.jinli.util.TimeUtil;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.donglaistd.jinli.constant.GameConstant.DAY_OF_MAX_HOUR;

@Document
public class LiveLimitList {
    @Id
    private ObjectId id = ObjectId.get();
    @Indexed
    private long limitDate;
    @Indexed
    private Constant.PlatformType platform;
    private Map<Integer, Set<String>> clockWhiteList = new HashMap<>();
    private long createTime;

    public LiveLimitList() {
    }

    public LiveLimitList(long limitDate, Constant.PlatformType platform) {
        this.limitDate = TimeUtil.getTimeDayStartTime(limitDate);
        this.platform = platform;
        this.createTime = System.currentTimeMillis();
        for (int hour = 0; hour <= DAY_OF_MAX_HOUR; hour++) {
            clockWhiteList.computeIfAbsent(hour, v -> new HashSet<>());
        }
    }

    public LiveLimitList(long limitDate, Constant.PlatformType platform,List<LiveTimeImportRequest> importRequests) {
        this.limitDate = TimeUtil.getTimeDayStartTime(limitDate);
        this.platform = platform;
        this.createTime = System.currentTimeMillis();
        for (LiveTimeImportRequest importRequest : importRequests) {
            clockWhiteList.computeIfAbsent(importRequest.getLimitStartHour(), v -> new HashSet<>()).addAll(importRequest.getStrLiveUserIds());
        }
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public long getLimitDate() {
        return limitDate;
    }

    public void setLimitDate(long limitDate) {
        this.limitDate = limitDate;
    }

    public Constant.PlatformType getPlatform() {
        return platform;
    }

    public void setPlatform(Constant.PlatformType platform) {
        this.platform = platform;
    }

    public Map<Integer, Set<String>> getClockWhiteList() {
        return clockWhiteList;
    }

    public void setClockWhiteList(Map<Integer, Set<String>> clockWhiteList) {
        this.clockWhiteList = clockWhiteList;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean allowLive(String liveUserId){
        int dayOfHour = TimeUtil.getDayOfHour();
        return clockWhiteList.getOrDefault(dayOfHour, new HashSet<>()).contains(liveUserId);
    }

    public long getLiveUserRecentLiveTime(String liveUserId){
        int dayOfHour = TimeUtil.getDayOfHour();
        OptionalInt resultHour = clockWhiteList.entrySet().stream().filter(entry -> entry.getValue().contains(liveUserId) && entry.getKey() <= dayOfHour)
                .mapToInt(Map.Entry::getKey).max();
        if(resultHour.isPresent()) return TimeUtil.getTimeByHour(resultHour.getAsInt());
        else return 0;
    }

    public void addClockWhiteList(Integer liveHour,String liveUserId){
        if(liveHour > DAY_OF_MAX_HOUR) return;
        clockWhiteList.computeIfAbsent(liveHour, v -> new HashSet<>()).add(liveUserId);
    }

    public boolean allowLiveByHour(int hour,String liveUserId){
        return clockWhiteList.getOrDefault(hour, new HashSet<>()).contains(liveUserId);
    }

    public boolean containsLiveUser(String liveUserId){
        return clockWhiteList.values().stream().anyMatch(ids -> ids.contains(liveUserId));
    }

    public long getLiveEndTimeStampByHour(int hour){
        return limitDate + TimeUnit.HOURS.toMillis(hour + 1) - 1;
    }

    public void cleanLiveUserRecord(String liveUserId) {
        for (Set<String> liveUserIds : clockWhiteList.values()) {
            liveUserIds.remove(liveUserId);
        }
    }
}
