package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Constant;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;

@Document
public class Feedback {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private String userId;
    @Field
    private String feedbackMessage;
    @Field
    private String systemVersion;
    @Field
    private Constant.MobilePhoneBrand brand;
    @Field
    private String mobileMobileModel;
    @Field
    private String appVersion;
    @Field
    private long createTime;

    public String getId() {
        return id.toString();
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public void setFeedbackMessage(String feedbackMessage) {
        this.feedbackMessage = feedbackMessage;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public Constant.MobilePhoneBrand getBrand() {
        return brand;
    }

    public void setBrand(Constant.MobilePhoneBrand brand) {
        this.brand = brand;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getMobileMobileModel() {
        return mobileMobileModel;
    }

    public void setMobileMobileModel(String mobileMobileModel) {
        this.mobileMobileModel = mobileMobileModel;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    private Feedback(String userId, String feedbackMessage) {
        this.userId = userId;
        this.feedbackMessage = feedbackMessage;
        this.createTime = System.currentTimeMillis();
    }

    public static Feedback newInstance(String userId, String feedbackMessage){
        return new Feedback(userId, feedbackMessage);
    }

}
