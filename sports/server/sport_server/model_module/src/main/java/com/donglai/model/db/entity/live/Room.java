package com.donglai.model.db.entity.live;


import com.alibaba.fastjson.annotation.JSONField;
import com.donglai.protocol.Constant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.donglai.common.constant.PathConstant.DEFAULT_ROOM_IMAGE_PATH;

@Data
@Document
@NoArgsConstructor
@ToString
public class Room implements Serializable {
    @Id
    private String id;
    @Field
    private String roomTitle;
    @Field
    @Indexed(unique = true)
    private String liveUserId;
    @Indexed(unique = true)
    private String userId;
    private String roomImage = DEFAULT_ROOM_IMAGE_PATH;
    private long createTime;
    private Constant.LivePattern pattern = Constant.LivePattern.LIVE_VIDEO;
    private long liveStartTime;

    //Live Properties
    @Transient
    private Set<String> audiences = new HashSet<>();
    @Transient
    private Map<String, Integer> giftRank = new ConcurrentHashMap<>();
    @Transient
    private List<BulletMessage> bulletMessages = new ArrayList<>();
    @Transient
    private Constant.LiveTag liveTag =  Constant.LiveTag.JIAOYOU;
    @Transient
    private String liveCode; //直播地址
    @Transient
    private int liveLineCode; //直播线路编号
    @Transient
    private boolean robotRoom = false;
    @Transient
    private boolean close;
    @Transient
    private String liveMusic;
    @Transient
    private String liveDomain = "szhdns.com";
    @Transient
    private String eventId; //赛事id
    @Transient
    private int eventGiftIncome = 2; //赛事收入
    @Transient
    private int eventPopularity = 3; //赛事热度

    public Room(String id,String liveUserId,String userId) {
        this.id = id;
        this.liveUserId = liveUserId;
        this.userId = userId;
        this.createTime = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public boolean isLive(){
        return liveStartTime > 0;
    }

    public void addAudience(String userId){
        audiences.add(userId);
    }

    public void removeAudience(String userId){
        audiences.remove(userId);
    }

    public boolean notContainsUser(String userId) {
        return !audiences.contains(userId);
    }

    public List<String> getAudienceLiveRankIdByLimit(int size){
        return giftRank.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(size).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public String getLiveDomain() {
        return liveDomain == null ? "" : liveDomain;
    }

    public void addBulletMessage(BulletMessage bulletMessage){
        bulletMessages.add(bulletMessage);
    }

    public void removeFirstBulletMessage(){
        if(bulletMessages.size()>0)
            bulletMessages.remove(0);
    }

    public void calcRank(String userId, int sendAmount) {
        giftRank.put(userId, giftRank.getOrDefault(userId, 0) + sendAmount);
    }

    public void cleanLiveRoom() {
        this.audiences.clear();
        this.giftRank.clear();
        this.bulletMessages.clear();
        this.liveCode = "";
        this.liveTag = null;
        this.liveLineCode = 0;
        this.liveStartTime = 0;
        this.liveDomain = "";
        this.liveMusic = "";
        this.close = false;
    }

    public void cleanEventInfo(){
        this.eventId = "";
        this.eventGiftIncome = 0;
        this.eventPopularity = 0;
    }
}
