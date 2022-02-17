package com.donglaistd.jinli.http.entity;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.CoinFlow;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.ComparatorUtil;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LiveUserInfo {
    public String displayName;

    public String id;

    public String liveUserId;

    public String realName;

    public String avatarUrl;

    public Constant.GenderType gender;

    public long birthDay;

    public String country;

    public String address;

    public String phoneNumber;

    public String email;

    public String contactWay;

    public List<String> images;

    public Constant.LiveStatus liveStatus;

    public String rejectReasons;

    public int userLevel;

    public long totalIncome;

    public int onlineTime;

    public long auditTime;

    public long totalCoin;

    public Constant.PlatformType platform;

    public long lastLoginTime;

    public List<Constant.PlatformType> sharedPlatform;

    public String getRejectReasons() {
        return rejectReasons;
    }

    public void setRejectReasons(String rejectReasons) {
        this.rejectReasons = rejectReasons;
    }

    public Constant.LiveStatus getLiveStatus() {
        return liveStatus;
    }

    public void setLiveStatus(Constant.LiveStatus liveStatus) {
        this.liveStatus = liveStatus;
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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Constant.GenderType getGender() {
        return gender;
    }

    public void setGender(Constant.GenderType gender) {
        this.gender = gender;
    }

    public long getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(long birthDay) {
        this.birthDay = birthDay;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactWay() {
        return contactWay;
    }

    public void setContactWay(String contactWay) {
        this.contactWay = contactWay;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setLiveUserId(String liveUserId) {
        this.liveUserId = liveUserId;
    }

    public LiveUserInfo(User user, LiveUser liveUser, CoinFlow coinFlow, int onlineTime) {
        this.avatarUrl = user.getAvatarUrl();
        this.displayName = user.getDisplayName();
        this.id = user.getId();
        this.liveUserId = liveUser.getId();
        this.totalIncome = coinFlow.getGiftIncome();
        this.auditTime = liveUser.getAuditTime();
        this.totalCoin = user.getGameCoin();
        this.onlineTime = onlineTime;
        this.platform = liveUser.getPlatformType();
        this.userLevel = user.getLevel();
        this.lastLoginTime = user.getLastLoginTime();
        this.sharedPlatform = liveUser.getSharedPlatform().stream().sorted(ComparatorUtil.getPlatformComparator()).collect(Collectors.toList());
        this.liveStatus = liveUser.getLiveStatus();
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public LiveUserInfo() {
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public List<Constant.PlatformType> getSharedPlatform() {
        return sharedPlatform;
    }

    public void setSharedPlatform(List<Constant.PlatformType> sharedPlatform) {
        this.sharedPlatform = sharedPlatform;
    }
}
