package com.donglaistd.jinli.database.entity.statistic;

import com.donglaistd.jinli.constant.StatisticEnum;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.util.HashMap;
import java.util.Map;

@Document
public class CommonStatisticInfo {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private long statisticTime;
    @Field
    private Map<StatisticEnum.StatisticItemEnum, Long> statisticItems = new HashMap<>();
    @Field
    private StatisticEnum statisticType;

    private CommonStatisticInfo(long statisticTime, StatisticEnum statisticType) {
        this.statisticTime = statisticTime;
        this.statisticType = statisticType;
    }

    public static CommonStatisticInfo newInstance(long statisticTime, StatisticEnum statisticType){
        return new CommonStatisticInfo(statisticTime,statisticType);
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public long getStatisticTime() {
        return statisticTime;
    }

    public void setStatisticTime(long statisticTime) {
        this.statisticTime = statisticTime;
    }

    public Map<StatisticEnum.StatisticItemEnum, Long> getStatisticItems() {
        return statisticItems;
    }

    public void setStatisticItems(Map<StatisticEnum.StatisticItemEnum, Long> statisticItems) {
        this.statisticItems = statisticItems;
    }

    public StatisticEnum getStatisticType() {
        return statisticType;
    }

    public void setStatisticType(StatisticEnum statisticType) {
        this.statisticType = statisticType;
    }

    public void putStatisticItem(StatisticEnum.StatisticItemEnum itemEnum,long value){
        this.statisticItems.put(itemEnum, value);
    }
    public long getItemValue(StatisticEnum.StatisticItemEnum itemType){
        return statisticItems.getOrDefault(itemType, 0L);
    }
}
