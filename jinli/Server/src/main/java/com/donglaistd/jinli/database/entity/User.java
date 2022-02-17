package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.constant.GameConstant;
import com.donglaistd.jinli.event.BankerQuitEvent;
import com.donglaistd.jinli.metadata.Metadata;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.MetaUtil;
import com.donglaistd.jinli.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static com.donglaistd.jinli.Constant.PlatformType.PLATFORM_Q;
import static com.donglaistd.jinli.Constant.PlatformType.PLATFORM_T;


@Document
@Cacheable("c1")
public class User implements Serializable {
    @Id
    private String id;
    @Field
    @Indexed(unique = true, sparse = true)
    private String liveUserId;
    @Field
    @Indexed(unique = true)
    private String accountName;
    @Field
    @Indexed(unique = true)
    private String displayName;
    @Field
    private String token;
    @Field
    private String phoneNumber;
    @Field
    private String avatarUrl;
    @Field
    private Date createDate;
    @Field
    private String mobileCode;
    @Field
    private long experience;
    @Transient
    private Date lastBulletChatTime;
    @Field
    private AtomicLong gameCoin = new AtomicLong(0);
    @Field
    private int level = 1;
    @Field
    private Constant.VipType vipType = Constant.VipType.LOCKED;
    @Transient
    private boolean isOnline = false;
    @Transient
    private boolean isQuitingBanker = false;
    @Transient
    private String currentRoomId;
    @Field
    private boolean isTourist;
    @Field
    private int modifyNameCount;
    @Transient
    private boolean quiteGame = false;
    @Field
    private long coinFlow = 0;
    @Field
    private boolean scriptUser = false;
    @Field
    private long lastLoginTime;
    @Field
    private AtomicLong goldBean = new AtomicLong(0);
    @Field
    private String lastMobileModel;
    @Field
    private String lastIp;
    @Field
    private Constant.PlatformType platformType = Constant.PlatformType.PLATFORM_JINLI;
    @Field
    @Indexed
    private String platformUserId;

    public User() {
        isTourist = false;
        createDate = new Date();
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public boolean isScriptUser() {
        return scriptUser;
    }

    public void setScriptUser(boolean scriptUser) {
        this.scriptUser = scriptUser;
    }

    public long getCoinFlow() {
        return coinFlow;
    }

    public void setCoinFlow(long coinFlow) {
        this.coinFlow = coinFlow;
    }

    public boolean isTourist() {
        return isTourist;
    }

    public void setTourist(boolean tourist) {
        isTourist = tourist;
    }

    @Override
    public String toString() {
        return "User{" + "userId=" + id + ", accountName='" + accountName + '\'' + ", displayName='" + displayName + '\'' + ", phoneNumber='" + phoneNumber + '\'' + ", avatarUrl" +
                "='" + avatarUrl + '\'' + ", level=" + level + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    public Constant.VipType getVipType() {
        return vipType;
    }

    public void setVipType(Constant.VipType vipType) {
        this.vipType = vipType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getLiveUserId() {
        return liveUserId;
    }

    public void setLiveUserId(String liveUserId) {
        this.liveUserId = liveUserId;
    }

    public Date getLastBulletChatTime() {
        return lastBulletChatTime;
    }

    public void setLastBulletChatTime(Date lastBulletChatTime) {
        this.lastBulletChatTime = lastBulletChatTime;
    }

    public synchronized long getGameCoin() {
        return gameCoin.get();
    }

    public synchronized void setGameCoin(long gameCoin) {
        this.gameCoin = new AtomicLong(gameCoin);
    }

    public String getAvatarUrl() {
        if (!Strings.isNullOrEmpty(avatarUrl)) {
            return avatarUrl;
        } else {
            if (Objects.equals(PLATFORM_T,platformType)) {
                return GameConstant.DEFAULT_PLATFORM_T_AVATAR_PATH;
            }
            return GameConstant.DEFAULT_AVATAR_PATH;
        }
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getMobileCode() {
        return mobileCode;
    }

    public void setMobileCode(String mobileCode) {
        this.mobileCode = mobileCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        if(Objects.equals(PLATFORM_T,platformType))
            return StringUtils.formatPlatform_T_DisplayName(displayName);
        else if(Objects.equals(PLATFORM_Q,platformType))
            return StringUtils.formatPlatform_Q_DisplayName(displayName);
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public synchronized void updateLevel(long addExp) {
        var myExp = this.experience + addExp;
        int needExp;
        do {
            Metadata.PlayerDefine define = MetaUtil.getPlayerDefineByCurrentLevel(this.level);
            if(define==null || define.getExp() <= 0) {
                return;
            }
            needExp = define.getExp();
            if (myExp >= needExp) {
                myExp = myExp - needExp;
                this.level++;
            }
        } while (myExp >= needExp);
        this.experience = myExp;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public synchronized void setQuitingBanker(boolean quitingBanker) {
        isQuitingBanker = quitingBanker;
    }

    public synchronized boolean checkQuitingBankerAndReset() {
        if (isQuitingBanker) {
            isQuitingBanker = false;
            if (isQuiteGame()) {
                quiteGame = false;
                EventPublisher.publish(new BankerQuitEvent(currentRoomId, this));
                cleanCurrentRoomId();
            }
            return true;
        }
        return false;
    }

    public String getCurrentRoomId() {
        return currentRoomId;
    }

    public void setCurrentRoomId(String currentRoomId) {
        this.currentRoomId = currentRoomId;
    }

    public Jinli.UserInfo toSummaryProto() {
        Jinli.UserInfo.Builder userInfo = Jinli.UserInfo.newBuilder().setLevel(this.level).setDisplayName(this.displayName).setUserId(getId()).setAvatarUrl(getAvatarUrl());
        return userInfo.build();
    }

    public boolean checkUserTypeIsLive() {
        return !StringUtils.isNullOrBlank(this.liveUserId);
    }

    public boolean updateVipByLevel() {
        Metadata.VipDefine levelVip = MetaUtil.getVipDefineByPlayerLevel(level);
        if(vipType.getNumber()!=levelVip.getVipIdValue()){
            this.vipType = Constant.VipType.forNumber(levelVip.getVipIdValue());
            return true;
        }
        return false;
    }

    public boolean canApplyConnect() {
        Metadata.VipDefine define = MetaUtil.getVipDefineByCurrentLevel(vipType.getNumber());
        if (Objects.isNull(define)) return false;
        Metadata.FunctionList function = define.getFunctionList();
        return function.getConnectLive();
    }

    public boolean canSendPrivateMessage() {
        Metadata.VipDefine define = MetaUtil.getVipDefineByCurrentLevel(vipType.getNumber());
        if (Objects.isNull(define)) return false;
        Metadata.FunctionList function = define.getFunctionList();
        return function.getPrivateMessage();
    }

    public void increaseModifyNameCount() {
        modifyNameCount++;
    }

    public int getModifyNameCount() {
        return modifyNameCount;
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }

    public boolean isQuiteGame() {
        return quiteGame;
    }

    public void setQuiteGame(boolean quiteGame) {
        this.quiteGame = quiteGame;
    }

    public void cleanCurrentRoomId() {
        this.currentRoomId = "";
    }

    public long addGameCoin(long coin) {
        return this.gameCoin.addAndGet(coin);
    }

    public long addGoldBean(long goldBean){
        return this.goldBean.addAndGet(goldBean);
    }

    public void setGoldBean(long goldBean) {
        this.goldBean = new AtomicLong(goldBean);
    }

    public long getGoldBean() {
        return goldBean.get();
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

    @JsonIgnore
    public boolean isPlatformUser() {
        if(this.platformType==null) return false;
        return !platformType.equals(Constant.PlatformType.PLATFORM_JINLI);
    }

    public Constant.PlatformType getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Constant.PlatformType platformType) {
        this.platformType = platformType;
    }

    public String getPlatformUserId() {
        return platformUserId;
    }

    public void setPlatformUserId(String platformUserId) {
        this.platformUserId = platformUserId;
    }

    @JsonIgnore
    public String getPlatformShowUserId() {
        if(isPlatformUser() && !StringUtils.isNullOrBlank(platformUserId))
            return platformUserId;
        return id;
    }

}
