package com.donglaistd.jinli.http.entity;

import com.donglaistd.jinli.Constant;

public class UserListData {

    public String displayName;

    public String id;

    public int level;

    public String phoneNumber;

    public Constant.VipType vipType;

    public long gameCoin;

    public long coinFlow;

    public long serviceFlow;

    public long recharge;

    public long lastLoginTime;

    public String lastMobileModel;

    public Constant.MobilePhoneBrand lastLoginBrand;

    public String lastIp;

    public long createDate;

    public boolean online;

    public String avatarUrl;

    public String liveUserId;

    public String roomId;

    public boolean LiveUser;

    public String inviteCode;

    public long totalCoinFlow;

    public Constant.PlatformType platform;

    public Constant.AccountStatue accountStatue;

    public long giftSpendAmount;

    public int platformTag;

    public String jinliUserId;

    public boolean isLiveUser() {
        return LiveUser;
    }

    public void setLiveUser(boolean liveUser) {
        LiveUser = liveUser;
    }

    public String getLiveUserId() {
        return liveUserId;
    }

    public void setLiveUserId(String liveUserId) {
        this.liveUserId = liveUserId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getLastMobileModel() {
        return lastMobileModel;
    }

    public void setLastMobileModel(String lastMobileModel) {
        this.lastMobileModel = lastMobileModel;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Constant.VipType getVipType() {
        return vipType;
    }

    public void setVipType(Constant.VipType vipType) {
        this.vipType = vipType;
    }

    public long getGameCoin() {
        return gameCoin;
    }

    public void setGameCoin(long gameCoin) {
        this.gameCoin = gameCoin;
    }

    public long getCoinFlow() {
        return coinFlow;
    }

    public void setCoinFlow(long coinFlow) {
        this.coinFlow = coinFlow;
    }

    public long getRecharge() {
        return recharge;
    }

    public void setRecharge(long recharge) {
        this.recharge = recharge;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Constant.AccountStatue getAccountStatue() {
        return accountStatue;
    }

    public void setAccountStatue(Constant.AccountStatue accountStatue) {
        this.accountStatue = accountStatue;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public long getTotalCoinFlow() {
        return totalCoinFlow;
    }

    public void setTotalCoinFlow(long totalCoinFlow) {
        this.totalCoinFlow = totalCoinFlow;
    }

    public Constant.PlatformType getPlatform() {
        return platform;
    }

    public void setPlatform(Constant.PlatformType platform) {
        this.platform = platform;
    }

    public long getServiceFlow() {
        return serviceFlow;
    }

    public void setServiceFlow(long serviceFlow) {
        this.serviceFlow = serviceFlow;
    }

    public Constant.MobilePhoneBrand getLastLoginBrand() {
        return lastLoginBrand;
    }

    public void setLastLoginBrand(Constant.MobilePhoneBrand lastLoginBrand) {
        this.lastLoginBrand = lastLoginBrand;
    }

    public long getGiftSpendAmount() {
        return giftSpendAmount;
    }

    public void setGiftSpendAmount(long giftSpendAmount) {
        this.giftSpendAmount = giftSpendAmount;
    }

    public int getPlatformTag() {
        return platformTag;
    }

    public void setPlatformTag(int platformTag) {
        this.platformTag = platformTag;
    }

    public String getJinliUserId() {
        return jinliUserId;
    }

    public void setJinliUserId(String jinliUserId) {
        this.jinliUserId = jinliUserId;
    }
}
