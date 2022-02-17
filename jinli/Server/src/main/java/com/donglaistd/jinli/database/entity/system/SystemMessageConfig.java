package com.donglaistd.jinli.database.entity.system;

import com.donglaistd.jinli.Constant;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;

import static com.donglaistd.jinli.constant.GameConstant.SERVER_ID;

@Document
public class SystemMessageConfig {
    @Id
    private String id;
    @Field
    private String rollMessage;
    @Field
    private long rollIntervalTime;
    @Field
    private long rollDisPlayStartTime;
    @Field
    private long rollDisPlayEndTime;
    @Field
    private String systemTipMessage;
    @Field
    @Indexed(unique = true)
    private Constant.PlatformType platformType;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getRollMessage() {
        return rollMessage;
    }

    public void setRollMessage(String rollMessage) {
        this.rollMessage = rollMessage;
    }

    public long getRollIntervalTime() {
        return rollIntervalTime;
    }

    public void setRollIntervalTime(long rollIntervalTime) {
        this.rollIntervalTime = rollIntervalTime;
    }

    public long getRollDisPlayStartTime() {
        return rollDisPlayStartTime;
    }

    public void setRollDisPlayStartTime(long rollDisPlayStartTime) {
        this.rollDisPlayStartTime = rollDisPlayStartTime;
    }

    public long getRollDisPlayEndTime() {
        return rollDisPlayEndTime;
    }

    public void setRollDisPlayEndTime(long rollDisPlayEndTime) {
        this.rollDisPlayEndTime = rollDisPlayEndTime;
    }

    public String getSystemTipMessage() {
        return systemTipMessage == null ? "" : systemTipMessage;
    }

    public void setSystemTipMessage(String systemTipMessage) {
        this.systemTipMessage = systemTipMessage;
    }

    private SystemMessageConfig() {
        this.id = SERVER_ID;
    }


    public boolean rollMessageIsExpire(){
        return System.currentTimeMillis() > rollDisPlayEndTime;
    }

    public SystemMessageConfig(String id, Constant.PlatformType platformType) {
        this.id = id;
        this.platformType = platformType;
    }

    public static SystemMessageConfig newInstance(){
        return new SystemMessageConfig();
    }

    public static SystemMessageConfig newInstance(Constant.PlatformType platformType){
        return new SystemMessageConfig(String.valueOf(platformType.getNumber()),platformType);
    }

    public Constant.PlatformType getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Constant.PlatformType platformType) {
        this.platformType = platformType;
    }

    @Override
    public String toString() {
        return "MessageCofig{" +
                "id='" + id + '\'' +
                ", rollMessage='" + rollMessage + '\'' +
                ", rollIntervalTime=" + rollIntervalTime +
                ", rollDisPlayStartTime=" + rollDisPlayStartTime +
                ", rollDisPlayEndTime=" + rollDisPlayEndTime +
                ", systemTipMessage='" + systemTipMessage + '\'' +
                '}';
    }

    public void cleanRollMessage(){
        rollMessage = "";
        rollDisPlayEndTime = 0;
        rollDisPlayStartTime = 0;
        rollIntervalTime = 0;
    }
}
