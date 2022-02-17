package com.donglaistd.jinli.database.entity.backoffice;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.util.TimeUtil;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.redis.core.index.Indexed;

import java.util.HashSet;
import java.util.Set;
@Document
@CompoundIndexes({
        @CompoundIndex(name = "record_index", def = "{'userId' : 1, 'recordTime': 1,'newUser': 1}",unique=true)})
public class UserDataStatistics {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private String userId;
    @Field
    @Indexed
    private long recordTime;
    @Field
    private String equipment;
    @Field
    private Constant.MobilePhoneBrand brand = Constant.MobilePhoneBrand.BRAND_OTHER;
    @Field
    private Constant.MobilePhoneResolution resolution = Constant.MobilePhoneResolution.PIXEL_OTHER;
    @Field
    private Constant.MobilePhoneOS os = Constant.MobilePhoneOS.OS_OTHER;
    @Field
    @Indexed
    private boolean newUser;
    @Field
    private boolean payingUser;
    @Field
    @Indexed
    private int activeDays;
    @Field
    private Set<String> ipList = new HashSet<>();
    @Field
    private long onlineTime;
    @Field
    private String mobileModel;
    @Field
    private String appVersion;
    @Field
    private Constant.PlatformType platformType;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public UserDataStatistics(String userId, String equipment, Constant.MobilePhoneBrand brand,
                              Constant.MobilePhoneResolution resolution, Constant.MobilePhoneOS os, boolean newUser, boolean payingUser) {
        this.userId = userId;
        this.equipment = equipment;
        this.brand = brand;
        this.resolution = resolution;
        this.os = os;
        this.newUser = newUser;
        this.payingUser = payingUser;
        this.recordTime = TimeUtil.getCurrentDayStartTime();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserDataStatistics(String userId, boolean newUser) {
        this.userId = userId;
        this.newUser = newUser;
        this.recordTime = TimeUtil.getCurrentDayStartTime();
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public Constant.MobilePhoneBrand getBrand() {
        return brand;
    }

    public void setBrand(Constant.MobilePhoneBrand brand) {
        this.brand = brand;
    }

    public Constant.MobilePhoneResolution getResolution() {
        return resolution;
    }

    public void setResolution(Constant.MobilePhoneResolution resolution) {
        this.resolution = resolution;
    }

    public Constant.MobilePhoneOS getOs() {
        return os;
    }

    public void setOs(Constant.MobilePhoneOS os) {
        this.os = os;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    public boolean isPayingUser() {
        return payingUser;
    }

    public void setPayingUser(boolean payingUser) {
        this.payingUser = payingUser;
    }

    public int getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(int activeDays) {
        this.activeDays = activeDays;
    }
    public UserDataStatistics() {
    }

    public static UserDataStatistics newInstance(String userId, String equipment, Constant.MobilePhoneBrand brand,
                                                 Constant.MobilePhoneResolution resolution, Constant.MobilePhoneOS os, boolean newUser, boolean payingUser){
        return new UserDataStatistics(userId, equipment, brand, resolution, os, newUser, payingUser);
    }

    public static UserDataStatistics newInstance(String userId, boolean newUser){
        return new UserDataStatistics(userId, newUser);
    }

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }

    public void addActiveDays(){
        this.activeDays++;
    }

    public void addIpRecord(String ip){
        this.ipList.add(ip);
    }

    public Set<String> getIpList() {
        return ipList;
    }

    public void setIpList(Set<String> ipList) {
        this.ipList = ipList;
    }

    public boolean hasLogin(){
        return !ipList.isEmpty();
    }

    public long getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(long onlineTime) {
        this.onlineTime = onlineTime;
    }

    public void addOnlineTime(long onlineTime){
        this.onlineTime += onlineTime;
    }

    public String getMobileModel() {
        return mobileModel == null ? "" : mobileModel;
    }

    public void setMobileModel(String mobileModel) {
        this.mobileModel = mobileModel;
    }

    public String getAppVersion() {
        return appVersion == null ? "" : appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Constant.PlatformType getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Constant.PlatformType platformType) {
        this.platformType = platformType;
    }
}
