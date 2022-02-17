package com.donglaistd.jinli.database.entity.zone;

import com.donglaistd.jinli.Constant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.concurrent.TimeUnit;

@Document
public class RecommendDiary {
    @Id
    private String diaryId;
    @Field
    @Indexed(unique = true)
    private int position;
    @Field
    private long startTime;
    @Field
    private Constant.DiaryRecommendTimeType recommendTime;
    @Field
    private long endTime;

    public RecommendDiary(String diaryId, int position, Constant.DiaryRecommendTimeType recommendTime) {
        this.diaryId = diaryId;
        this.position = position;
        this.startTime = System.currentTimeMillis();
        this.recommendTime = recommendTime;
        this.endTime = (startTime + getRecommendTimeByRecommendTimeType());
    }

    public static RecommendDiary newInstance(String diaryId, int position, Constant.DiaryRecommendTimeType recommendTime) {
        return new RecommendDiary(diaryId, position, recommendTime);
    }

    public boolean isRecommend(){
        return System.currentTimeMillis() <= endTime;
    }

    public String getDiaryId() {
        return diaryId;
    }

    public void setDiaryId(String diaryId) {
        this.diaryId = diaryId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public Constant.DiaryRecommendTimeType getRecommendTime() {
        return recommendTime;
    }

    public void setRecommendTime(Constant.DiaryRecommendTimeType recommendTime) {
        this.recommendTime = recommendTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getRecommendTimeByRecommendTimeType(){
        switch (this.recommendTime){
            case RECOMMEND_SIX_HOUR:
                return TimeUnit.HOURS.toMillis(6);
            case RECOMMEND_TWELVE_HOUR:
                return TimeUnit.HOURS.toMillis(12);
            case RECOMMEND_TWENTY_FOUR_HOUR:
                return TimeUnit.HOURS.toMillis(24);
            case RECOMMEND_FORTY_EIGHT_HOUR:
                return TimeUnit.HOURS.toMillis(48);
        }
        return 0;
    }
}
