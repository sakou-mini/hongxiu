package com.donglaistd.jinli.database.entity.rank;

import com.donglaistd.jinli.Constant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Convert;
import java.io.Serializable;

@Document(collection = "rank")
public class Rank implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String userId;
    private Constant.RankType rankType;
    private long time;
    private int arrow;
    @Convert(converter = org.bson.Document.class)
    private org.bson.Document extra;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public Rank(String userId, Constant.RankType rankType, long time, org.bson.Document extra) {
        this.userId = userId;
        this.rankType = rankType;
        this.time = time;
        this.extra = extra;
    }

    public Rank() {
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Constant.RankType getRankType() {
        return rankType;
    }

    public void setRankType(Constant.RankType rankType) {
        this.rankType = rankType;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public org.bson.Document getExtra() {
        return extra;
    }

    public void setExtra(org.bson.Document extra) {
        this.extra = extra;
    }

    public int getArrow() {
        return arrow;
    }

    public void setArrow(int arrow) {
        this.arrow = arrow;
    }

    @Override
    public String toString() {
        return "Rank{" +
                "id='" + id + '\'' +
                ", userId=" + userId +
                ", rankType=" + rankType +
                ", time=" + time +
                ", arrow=" + arrow +
                ", extra=" + extra +
                '}';
    }
}
