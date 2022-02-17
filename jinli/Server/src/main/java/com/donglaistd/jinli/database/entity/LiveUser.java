package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.metadata.Metadata;
import com.donglaistd.jinli.util.MetaUtil;
import com.donglaistd.jinli.util.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.*;

import static com.donglaistd.jinli.constant.LiveUserConstant.DefaultQuickChat;

@Document
public class LiveUser implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    @Field
    @Indexed(unique = true)
    private String userId;
    @Field
    @Indexed(unique = true, sparse = true)
    private String roomId;
    @Field
    private int level = 1;
    @Field
    private long exp;
    @Transient
    private String playingGameId;
    @Transient
    private LiveGameInfo liveGameInfo;
    @Field
    private Constant.LiveStatus liveStatus;
    @Field
    private String liveUrl;
    @Field
    private Constant.GenderType gender = Constant.GenderType.FEMALE;
    @Transient
    private String rtmpCode = "";
    @Transient
    private boolean rtmpLive = false;
    @Field
    private String idImageURL;
    @Field
    private String identityNumber;
    @Field
    private String realName;
    @Field
    @Indexed
    private boolean scriptLiveUser = false;
    @Field
    private List<String> images = new ArrayList<>();
    @Field
    private String country;
    @Field
    private String address;
    @Field
    private String phoneNumber;
    @Field
    private String email;
    @Field
    private String contactWay;
    @Field
    private long birthDay;
    @Field
    private long lastLiveTime;
    @Field
    private String bankName;
    @Field
    private String bankCard;
    @Field
    private Constant.PlatformType platformType = Constant.PlatformType.PLATFORM_JINLI;
    @Field
    private String liveNotice;
    @Field
    private long auditTime;
    @Field
    private long applyTime;
    @Field
    private long banTime;
    @Field
    private Set<Constant.LiveUserPermission> disablePermissions = new HashSet<>(0);
    @Field
    private boolean addWhiteList;
    @Field
    private Set<Constant.PlatformType> sharedPlatform = new HashSet<>();
    @Field
    private String quickChat = DefaultQuickChat;


    public LiveUser() {
        this.applyTime = System.currentTimeMillis();
    }

    public LiveUser(String id, String userId, Constant.LiveStatus liveStatus, String liveUrl, Constant.PlatformType platform) {
        this.id = id;
        this.userId = userId;
        this.liveStatus = liveStatus;
        this.liveUrl = liveUrl;
        this.platformType = platform;
        this.applyTime = System.currentTimeMillis();
    }

    public boolean isScriptLiveUser() {
        return scriptLiveUser;
    }

    public void setScriptLiveUser(boolean scriptLiveUser) {
        this.scriptLiveUser = scriptLiveUser;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdImageURL() {
        return idImageURL;
    }

    public void setIdImageURL(String idImageURL) {
        this.idImageURL = idImageURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiveUser liveUser = (LiveUser) o;
        return Objects.equals(id, liveUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLiveUrl() {
        return liveUrl;
    }

    public void setLiveUrl(String liveUrl) {
        this.liveUrl = liveUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Constant.LiveStatus getLiveStatus() {
        return liveStatus;
    }

    public void setLiveStatus(Constant.LiveStatus liveStatus) {
        this.liveStatus = liveStatus;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getRtmpCode() {
        return rtmpCode;
    }

    public void setRtmpCode(String rtmpCode) {
        this.rtmpCode = rtmpCode;
    }

    public boolean isRtmpLive() {
        return rtmpLive;
    }

    public void setRtmpLive(boolean rtmpLive) {
        this.rtmpLive = rtmpLive;
    }

    public Constant.GenderType getGender() {
        return gender;
    }

    public void setGender(Constant.GenderType gender) {
        this.gender = gender;
    }

    public long getLastLiveTime() {
        return lastLiveTime;
    }

    public void setLastLiveTime(long lastLiveTime) {
        this.lastLiveTime = lastLiveTime;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    @Override
    public String toString() {
        return "LiveUser{" + "id=" + id + ", userId='" + userId + '\'' + ", roomId='" + roomId + '\'' + ", level=" + level + ", exp=" + exp + ", playingGame=" + playingGameId + ","
                + " liveStatus=" + liveStatus + ", liveUrl='" + liveUrl + '\'' + ", rtmpCode='" + rtmpCode + '\'' + ", rtmpLive=" + rtmpLive + '}';
    }

    public String getPlayingGameId() {
        return playingGameId;
    }

    public void setPlayingGameId(String playingGameId) {
        this.playingGameId = playingGameId;
    }

    public void updateLevel(long addExp) {
        var myExp = this.exp + addExp;
        int needExp;
        do {
            Metadata.PlayerDefine define = MetaUtil.getPlayerDefineByCurrentLevel(this.level);
            needExp = define.getAnnouncerExp();
            if (myExp >= needExp) {
                myExp = myExp - needExp;
                this.level++;
            }
        } while (myExp >= needExp);
        this.exp = myExp;
    }

    public long getExp() {
        return exp;
    }

    public void cleanLive() {
        this.playingGameId = null;
        this.setLiveGameInfo(null);
        if (!Objects.equals(Constant.LiveStatus.LIVE_BAN, liveStatus)) {
            setLiveStatus(Constant.LiveStatus.OFFLINE);
        }
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
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

    public long getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(long birthDay) {
        this.birthDay = birthDay;
    }

    public LiveGameInfo getLiveGameInfo() {
        return liveGameInfo;
    }

    public void setLiveGameInfo(LiveGameInfo liveGameInfo) {
        this.liveGameInfo = liveGameInfo;
    }

    public Constant.PlatformType getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Constant.PlatformType platformType) {
        this.platformType = platformType;
    }

    public String getLiveNotice() {
        return liveNotice;
    }

    public void setLiveNotice(String liveNotice) {
        this.liveNotice = liveNotice;
    }

    public long getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(long auditTime) {
        this.auditTime = auditTime;
    }

    public long getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(long applyTime) {
        this.applyTime = applyTime;
    }

    public Set<Constant.LiveUserPermission> getDisablePermissions() {
        return disablePermissions == null ? new HashSet<>(0) : disablePermissions;
    }

    public void setDisablePermissions(Set<Constant.LiveUserPermission> disablePermissions) {
        this.disablePermissions = disablePermissions;
    }

    public boolean containsDisablePermission(Constant.LiveUserPermission permission) {
        return disablePermissions != null && this.disablePermissions.contains(permission);
    }

    public long getBanTime() {
        return banTime;
    }

    public void setBanTime(long banTime) {
        this.banTime = banTime;
    }

    public boolean isAddWhiteList() {
        return addWhiteList;
    }

    public void setAddWhiteList(boolean liveLimit) {
        this.addWhiteList = liveLimit;
    }


    public void addSharedPlatform(Constant.PlatformType platform) {
        if(!Objects.equals(platform, platformType))
            sharedPlatform.add(platform);
    }

    public Set<Constant.PlatformType> getSharedPlatform() {
        return sharedPlatform;
    }

    public void setSharedPlatform(Set<Constant.PlatformType> sharedPlatform) {
        this.sharedPlatform = sharedPlatform;
    }

    public String getQuickChat() {
        if(StringUtils.isNullOrBlank(quickChat)) return "";
        return quickChat;
    }

    public void setQuickChat(String quickChat) {
        this.quickChat = quickChat;
    }
}
