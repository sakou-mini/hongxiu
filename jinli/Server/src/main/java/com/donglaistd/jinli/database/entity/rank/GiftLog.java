package com.donglaistd.jinli.database.entity.rank;


import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Document
public class GiftLog implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    @Indexed
    private String senderId;
    @Field
    @Indexed
    private String receiveId;
    @Field
    private int sendAmount;
    @Field
    private long createTime;
    @Field
    private String giftId;
    @Field
    private int sendNum;
    @Field
    private Constant.PlatformType platformType = Constant.PlatformType.PLATFORM_JINLI;

    public GiftLog() {
    }

    public String getId() {
        return id.toString();
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId;
    }

    public int getSendAmount() {
        return sendAmount;
    }

    public void setSendAmount(int sendAmount) {
        this.sendAmount = sendAmount;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public GiftLog(String senderId, String receiveId, int sendAmount, long createTime, String giftId, int sendNum,Constant.PlatformType platformType) {
        this.senderId = senderId;
        this.receiveId = receiveId;
        this.sendAmount = sendAmount;
        this.createTime = createTime;
        this.giftId = giftId;
        this.sendNum = sendNum;
        this.platformType = platformType;
    }

    public static GiftLog newInstance(String senderId, String receiveId, int sendAmount,String giftId,int sendNum) {
        return new GiftLog(senderId,receiveId,sendAmount,System.currentTimeMillis(),giftId,sendNum,Constant.PlatformType.PLATFORM_JINLI);
    }

    public static GiftLog newInstance(String senderId, String receiveId, int sendAmount,String giftId,int sendNum,Constant.PlatformType platformType) {
        return new GiftLog(senderId,receiveId,sendAmount,System.currentTimeMillis(),giftId,sendNum,platformType);
    }

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public int getSendNum() {
        return sendNum;
    }

    public void setSendNum(int sendNum) {
        this.sendNum = sendNum;
    }

    public Constant.PlatformType getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Constant.PlatformType platformType) {
        this.platformType = platformType;
    }

    @Override
    public String toString() {
        return "GiftLog{" +
                "id=" + id +
                ", senderId='" + senderId + '\'' +
                ", receiveId='" + receiveId + '\'' +
                ", sendAmount=" + sendAmount +
                ", createTime=" + createTime +
                ", giftId='" + giftId + '\'' +
                ", sendNum=" + sendNum +
                '}';
    }

    public Jinli.GiftRecord.Builder toProto(){
        return Jinli.GiftRecord.newBuilder().setGiftId(giftId).setSendTime(createTime).setSendNum(sendNum).setCoinAmount(sendAmount);
    }
}
