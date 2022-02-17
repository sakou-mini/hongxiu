package com.donglaistd.jinli.database.entity.backoffice;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.system.SystemMessageConfig;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
@Document
public class ChangeSystemTipMessageRecord {
    @Id
    private String id = ObjectId.get().toString();
    @Field
    private long recordTime;
    @Field
    private String oldMessage;
    @Field
    private String newMessage;
    @Field
    private Constant.PlatformType platform;
    @Field
    private String backOfficeUserId;
    @Transient
    private String backOfficeAccountName;
    @Transient
    private boolean takeEffect;

    public ChangeSystemTipMessageRecord(String oldMessage, SystemMessageConfig newMessageConfig, String backOfficeUserId) {
        this.recordTime = System.currentTimeMillis();
        this.oldMessage = oldMessage;
        this.newMessage = newMessageConfig.getSystemTipMessage();
        this.platform = newMessageConfig.getPlatformType();
        this.backOfficeUserId = backOfficeUserId;
    }

    public ChangeSystemTipMessageRecord() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }

    public String getOldMessage() {
        return oldMessage;
    }

    public void setOldMessage(String oldMessage) {
        this.oldMessage = oldMessage;
    }

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }

    public Constant.PlatformType getPlatform() {
        return platform;
    }

    public void setPlatform(Constant.PlatformType platform) {
        this.platform = platform;
    }

    public String getBackOfficeUserId() {
        return backOfficeUserId;
    }

    public void setBackOfficeUserId(String backOfficeUserId) {
        this.backOfficeUserId = backOfficeUserId;
    }

    public String getBackOfficeAccountName() {
        return backOfficeAccountName;
    }

    public void setBackOfficeAccountName(String backOfficeAccountName) {
        this.backOfficeAccountName = backOfficeAccountName;
    }

    public boolean isTakeEffect() {
        return takeEffect;
    }

    public void setTakeEffect(boolean takeEffect) {
        this.takeEffect = takeEffect;
    }
}
