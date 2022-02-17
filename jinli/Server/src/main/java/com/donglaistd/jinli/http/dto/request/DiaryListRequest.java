package com.donglaistd.jinli.http.dto.request;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.util.TimeUtil;

import java.util.Objects;

public class DiaryListRequest {
    public String userId;
    public Long startTime;
    public Long endTime;
    public int diaryStatue;
    public int recommendValue;
    public int page;
    public int size;

    public DiaryListRequest(String userId, Long startTime, Long endTime, int diaryStatue, int recommendValue, int page, int size) {
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.diaryStatue = diaryStatue;
        this.recommendValue = recommendValue;
        this.page = page;
        this.size = size;
    }

    public DiaryListRequest() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getStartTime() {
        if(Objects.nonNull(startTime)) startTime = TimeUtil.getTimeDayStartTime(startTime);
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        if(Objects.nonNull(endTime)) endTime = TimeUtil.getDayEndTime(endTime);
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public int getDiaryStatue() {
        return diaryStatue;
    }

    public void setDiaryStatue(int diaryStatue) {
        this.diaryStatue = diaryStatue;
    }

    public int getRecommendValue() {
        return recommendValue;
    }

    public void setRecommendValue(int recommendValue) {
        this.recommendValue = recommendValue;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Constant.DiaryStatue getStatueType() {
        return Constant.DiaryStatue.forNumber(diaryStatue);
    }

    public Boolean getRecommendType(){
        Boolean recommend = null;
        if(recommendValue > 0) recommend = Objects.equals(recommendValue,1);
        return recommend;
    }
}
