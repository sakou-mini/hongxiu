package com.donglaistd.jinli.database.entity.backoffice;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.constant.LiveUserApproveStatue;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;



@Document
public class LiveUserApproveRecord implements Serializable {
    @Id
    private ObjectId id = ObjectId.get();

    @Field
    private String backOfficeName;
    @Field
    private String approveLiveUserId;
    @Field
    private String userId;
    @Field
    private boolean approveState;
    @Field
    private String rejectReasons;
    @Field
    private Constant.PlatformType platform;
    @Field
    private LiveUserApproveStatue statue;
    @Field
    private long applyDate;
    @Field
    private Date approveDate;

    public Date getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(Date approveDate) {
        this.approveDate = approveDate;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getBackOfficeName() {
        return backOfficeName;
    }

    public void setBackOfficeName(String backOfficeName) {
        this.backOfficeName = backOfficeName;
    }

    public boolean isApproveState() {
        return approveState;
    }

    public void setRejectReasons(String rejectReasons) {
        this.rejectReasons = rejectReasons;
    }

    public LiveUserApproveRecord() {
    }

    public LiveUserApproveRecord(String approveLiveUserId,String userId, Constant.PlatformType platform, LiveUserApproveStatue statue) {
        this.approveLiveUserId = approveLiveUserId;
        this.userId = userId;
        this.platform = platform;
        this.statue = statue;
        this.applyDate = System.currentTimeMillis();
    }

    public static LiveUserApproveRecord newInstance(String approveLiveUserId,String userId, Constant.PlatformType platform, LiveUserApproveStatue statue){
        return new LiveUserApproveRecord(approveLiveUserId,userId,platform,statue);
    }

    public void setApproveState(boolean state){this.approveState = state;}

    public boolean getApproveState() {return this.approveState;}

    public void setRejectionReasons(String rejectionReasons){this.rejectReasons = rejectionReasons;}

    public String getRejectReasons(){return this.rejectReasons;}

    public String getApproveLiveUserId() {
        return approveLiveUserId;
    }

    public void setApproveLiveUserId(String approveLiveUserId) {
        this.approveLiveUserId = approveLiveUserId;
    }

    public Constant.PlatformType getPlatform() {
        return platform;
    }

    public void setPlatform(Constant.PlatformType platform) {
        this.platform = platform;
    }

    public LiveUserApproveStatue getStatue() {
        return statue;
    }

    public void setStatue(LiveUserApproveStatue statue) {
        this.statue = statue;
    }

    public long getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(long applyDate) {
        this.applyDate = applyDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void approveLiveUser(LiveUserApproveStatue statue ,String backOfficeName){
        this.backOfficeName = backOfficeName;
        this.statue = statue;
        this.approveDate = new Date();
    }
}
