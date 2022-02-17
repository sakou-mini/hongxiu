package com.donglaistd.jinli.database.entity.backoffice;

import com.donglaistd.jinli.Constant;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

public class OfficialLiveRecord {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private Long roomCreateDate;
    @Field
    private Long roomCloseDate;
    @Field
    private String backOfficeName;
    @Field
    @Indexed
    private String liveUserId;
    @Field
    private boolean open;
    @Field
    private String roomId;
    @Field
    private String roomName;
    @Field
    private String roomDisplayId;
    @Field
    private Constant.GameType gameType;
    @Field
    private long gamePlayCoinAmount;
    @Field
    private long giftCoinAmount;
    @Field
    private long historyCount;
    @Field
    private long currentCount;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Long getRoomCreateDate() {
        return roomCreateDate;
    }

    public void setRoomCreateDate(Long roomCreateDate) {
        this.roomCreateDate = roomCreateDate;
    }

    public Long getRoomCloseDate() {
        return roomCloseDate;
    }

    public void setRoomCloseDate(Long roomCloseDate) {
        this.roomCloseDate = roomCloseDate;
    }

    public String getBackOfficeName() {
        return backOfficeName;
    }

    public void setBackOfficeName(String backOfficeName) {
        this.backOfficeName = backOfficeName;
    }

    public String getLiveUserId() {
        return liveUserId;
    }

    public void setLiveUserId(String liveUserId) {
        this.liveUserId = liveUserId;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomDisplayId() {
        return roomDisplayId;
    }

    public void setRoomDisplayId(String roomDisplayId) {
        this.roomDisplayId = roomDisplayId;
    }

    public Constant.GameType getGameType() {
        return gameType;
    }

    public void setGameType(Constant.GameType gameType) {
        this.gameType = gameType;
    }

    public long getGamePlayCoinAmount() {
        return gamePlayCoinAmount;
    }

    public void setGamePlayCoinAmount(long gamePlayCoinAmount) {
        this.gamePlayCoinAmount = gamePlayCoinAmount;
    }

    public long getGiftCoinAmount() {
        return giftCoinAmount;
    }

    public void setGiftCoinAmount(long giftCoinAmount) {
        this.giftCoinAmount = giftCoinAmount;
    }

    public long getHistoryCount() {
        return historyCount;
    }

    public void setHistoryCount(long historyCount) {
        this.historyCount = historyCount;
    }

    public long getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(long currentCount) {
        this.currentCount = currentCount;
    }
}
