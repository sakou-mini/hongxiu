package com.donglaistd.jinli.database.entity.rank;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.util.Pair;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "giftRank")
public class GiftRank implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private long createTime;
    @Field
    private Constant.QueryTimeType timeType;
    @Field
    private Constant.RankType rankType;
    @Field
    private List<Pair<String, Integer>> infos;
    @Field
    private Map<Constant.PlatformType, List<Pair<String, Integer>>> platformRankInfo = new HashMap<>();

    public String getId() {
        return id.toString();
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Constant.QueryTimeType getTimeType() {
        return timeType;
    }

    public void setTimeType(Constant.QueryTimeType timeType) {
        this.timeType = timeType;
    }

    public List<Pair<String, Integer>> getInfos() {
        return infos;
    }

    public void setInfos(List<Pair<String, Integer>> infos) {
        this.infos = infos;
    }

    public Constant.RankType getRankType() {
        return rankType;
    }

    public void setRankType(Constant.RankType rankType) {
        this.rankType = rankType;
    }

    public GiftRank(long createTime, Constant.QueryTimeType timeType, Constant.RankType rankType, Map<Constant.PlatformType, List<Pair<String, Integer>>> platformRankInfo) {
        this.createTime = createTime;
        this.timeType = timeType;
        this.rankType = rankType;
        this.platformRankInfo = platformRankInfo;
    }

    public static GiftRank newInstance(long createTime, Constant.QueryTimeType timeType, Constant.RankType rankType, Map<Constant.PlatformType, List<Pair<String, Integer>>> platformRankInfo) {
        return new GiftRank(createTime, timeType, rankType, platformRankInfo);
    }

    public Map<Constant.PlatformType, List<Pair<String, Integer>>> getPlatformRankInfo() {
        return platformRankInfo;
    }

    public List<Pair<String, Integer>> getRankInfoByPlatformType(Constant.PlatformType platformType){
        return platformRankInfo.getOrDefault(platformType, new ArrayList<>());
    }

    public GiftRank() {
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public GiftRank(long createTime, Constant.QueryTimeType timeType, Constant.RankType rankType, List<Pair<String, Integer>> infos) {
        this.createTime = createTime;
        this.timeType = timeType;
        this.rankType = rankType;
        this.infos = infos;
    }

    public void setPlatformRankInfo(Map<Constant.PlatformType, List<Pair<String, Integer>>> platformRankInfo) {
        this.platformRankInfo = platformRankInfo;
    }

    public static GiftRank newInstance(long createTime, Constant.QueryTimeType timeType, Constant.RankType rankType, List<Pair<String, Integer>> infos) {
        return new GiftRank(createTime, timeType, rankType, infos);
    }

    public void updateRank(Constant.PlatformType platform, List<Pair<String, Integer>> rankInfo){
        this.platformRankInfo.put(platform, rankInfo);
    }

    @Override
    public String toString() {
        return "GiftRank{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", timeType=" + timeType +
                ", rankType=" + rankType +
                ", infos=" + infos +
                ", platformRankInfo=" + platformRankInfo +
                '}';
    }
}
