package com.donglaistd.jinli.database.entity.backoffice;

import com.donglaistd.jinli.http.entity.GuessBetInfo;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

public class GuessWagerRecord {

    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private String orderNum;
    @Field
    private String userId;
    @Field
    private String guessId;
    @Field
    private Long wagerTime;
    @Field
    private Map<String, GuessBetInfo> wagerList = new HashMap<>();
    @Field
    private Long total;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGuessId() {
        return guessId;
    }

    public void setGuessId(String guessId) {
        this.guessId = guessId;
    }

    public Long getWagerTime() {
        return wagerTime;
    }

    public void setWagerTime(Long wagerTime) {
        this.wagerTime = wagerTime;
    }

    public Map<String, GuessBetInfo> getWagerList() {
        return wagerList;
    }

    public void setWagerList(Map<String, GuessBetInfo> wagerList) {
        this.wagerList = wagerList;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public GuessBetInfo getWagerListItem(String key){
        return this.wagerList.get(key);
    }
}
