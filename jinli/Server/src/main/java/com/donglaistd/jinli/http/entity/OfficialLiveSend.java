package com.donglaistd.jinli.http.entity;

import com.donglaistd.jinli.Constant;

public class OfficialLiveSend {

    public String roomName;

    public String roomId;

    public Constant.GameType gameType;

    public long giftTurnover;

    public long gameTurnover;

    public int roomPeople;

    public int nowRoomPeople;

    public Long roomCreateDate;

    public Long roomCloseDate;

    public String backOfficeName;

    public String liveUserId;

    public String roomDisplayId;

    public String getRoomDisplayId() {
        return roomDisplayId;
    }

    public void setRoomDisplayId(String roomDisplayId) {
        this.roomDisplayId = roomDisplayId;
    }

    public String getLiveUserId() {
        return liveUserId;
    }

    public void setLiveUserId(String liveUserId) {
        this.liveUserId = liveUserId;
    }

    public String getBackOfficeName() {
        return backOfficeName;
    }

    public void setBackOfficeName(String backOfficeName) {
        this.backOfficeName = backOfficeName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Constant.GameType getGameType() {
        return gameType;
    }

    public void setGameType(Constant.GameType gameType) {
        this.gameType = gameType;
    }

    public long getGiftTurnover() {
        return giftTurnover;
    }

    public void setGiftTurnover(long giftTurnover) {
        this.giftTurnover = giftTurnover;
    }

    public long getGameTurnover() {
        return gameTurnover;
    }

    public void setGameTurnover(long gameTurnover) {
        this.gameTurnover = gameTurnover;
    }

    public int getRoomPeople() {
        return roomPeople;
    }

    public void setRoomPeople(int roomPeople) {
        this.roomPeople = roomPeople;
    }

    public int getNowRoomPeople() {
        return nowRoomPeople;
    }

    public void setNowRoomPeople(int nowRoomPeople) {
        this.nowRoomPeople = nowRoomPeople;
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
}
