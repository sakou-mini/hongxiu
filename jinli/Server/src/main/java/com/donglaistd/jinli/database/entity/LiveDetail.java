package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Constant;

import java.util.ArrayList;
import java.util.List;

public class LiveDetail {
    public String userId;
    public String displayName;
    public String liveUserId;
    public String roomId;
    public String roomDisplayId;
    public int audienceCount;
    public int audienceNum;
    public int robotNum;
    public Constant.GameType gameType;
    public long gameIncome;
    public long giftIncome;
    public String roomTitle;
    public int activeNum;
    public int chatNum;
    public long liveStartTime;
    public boolean hot;
    public Constant.Pattern livePattern = Constant.Pattern.LIVE_VIDEO;
    public List<Constant.PlatformType> sharedPlatform = new ArrayList<>();
    public LiveDetail setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public LiveDetail setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public LiveDetail setLiveUserId(String liveUserId) {
        this.liveUserId = liveUserId;
        return this;
    }

    public LiveDetail setRoomId(String roomId) {
        this.roomId = roomId;
        return this;
    }

    public LiveDetail setRoomDisplayId(String roomDisplayId) {
        this.roomDisplayId = roomDisplayId;
        return this;
    }

    public LiveDetail setAudienceCount(int audienceCount) {
        this.audienceCount = audienceCount;
        return this;
    }

    public LiveDetail setAudienceNum(int audienceNum) {
        this.audienceNum = audienceNum;
        return this;
    }

    public LiveDetail setRobotNum(int robotNum) {
        this.robotNum = robotNum;
        return this;
    }

    public LiveDetail setGameType(Constant.GameType gameType) {
        this.gameType = gameType;
        return this;
    }

    public LiveDetail setGameIncome(long gameIncome) {
        this.gameIncome = gameIncome;
        return this;
    }

    public LiveDetail setGiftIncome(long giftIncome) {
        this.giftIncome = giftIncome;
        return this;
    }

    public LiveDetail setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
        return this;
    }

    public LiveDetail setActiveNum(int activeNum) {
        this.activeNum = activeNum;
        return this;
    }

    public LiveDetail setHot(boolean hot) {
        this.hot = hot;
        return this;
    }

    public Constant.Pattern getLivePattern() {
        return livePattern;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLiveUserId() {
        return liveUserId;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getRoomDisplayId() {
        return roomDisplayId;
    }

    public int getAudienceCount() {
        return audienceCount;
    }

    public int getAudienceNum() {
        return audienceNum;
    }

    public int getRobotNum() {
        return robotNum;
    }

    public Constant.GameType getGameType() {
        return gameType;
    }

    public long getGameIncome() {
        return gameIncome;
    }

    public long getGiftIncome() {
        return giftIncome;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public int getActiveNum() {
        return activeNum;
    }

    public int getChatNum() {
        return chatNum;
    }

    public LiveDetail setChatNum(int chatNum) {
        this.chatNum = chatNum;
        return this;

    }

    public long getLiveStartTime() {
        return liveStartTime;
    }

    public LiveDetail setLiveStartTime(long liveStartTime) {
        this.liveStartTime = liveStartTime;
        return this;
    }

    public boolean isHot() {
        return hot;
    }

    public LiveDetail setLivePattern(Constant.Pattern livePattern) {
        this.livePattern = livePattern;
        return this;
    }

    public List<Constant.PlatformType> getSharedPlatform() {
        return sharedPlatform;
    }

    public LiveDetail setSharedPlatform(List<Constant.PlatformType> sharedPlatform) {
        this.sharedPlatform = sharedPlatform;
        return this;
    }
}
