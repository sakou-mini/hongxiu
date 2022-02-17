package com.donglaistd.jinli.http.entity.statistic;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.rank.GiftLog;

public class UserGiftDetailData {
    public String userId;
    public String userName;
    public int vipLevel;
    public String giftOfRoomId;
    public String liveUserId;
    public String liveUserName;
    public String giftId;
    public int sendNum;
    public int totalPrice;
    public long sendTime;
    public Constant.VipType vipType;
    public int platformTag;

    public UserGiftDetailData(User senderUser , GiftLog giftLog) {
        this.userId = senderUser.getPlatformShowUserId();
        this.userName = senderUser.getDisplayName();
        this.vipLevel = senderUser.getVipType().getNumber();
        this.vipType = senderUser.getVipType();
        this.giftId = giftLog.getGiftId();
        this.sendNum = giftLog.getSendNum();
        this.totalPrice = giftLog.getSendAmount();
        this.sendTime = giftLog.getCreateTime();
    }

    public UserGiftDetailData(User senderUser, Room room, GiftLog giftLog) {
        this.userId = senderUser.getPlatformShowUserId();
        this.userName = senderUser.getDisplayName();
        this.vipLevel = senderUser.getVipType().getNumber();
        this.vipType = senderUser.getVipType();
        this.giftOfRoomId = room.getDisplayId();
        this.liveUserId = room.getLiveUserId();
        this.giftId = giftLog.getGiftId();
        this.sendNum = giftLog.getSendNum();
        this.totalPrice = giftLog.getSendAmount();
        this.sendTime = giftLog.getCreateTime();
    }

    public UserGiftDetailData(User senderUser, User receiveUser,Room room, GiftLog giftLog,int platformTag) {
        this.userId = senderUser.getPlatformShowUserId();
        this.userName = senderUser.getDisplayName();
        this.vipLevel = senderUser.getVipType().getNumber();
        this.vipType = senderUser.getVipType();
        this.giftOfRoomId = room.getDisplayId();
        this.liveUserId = room.getLiveUserId();
        this.giftId = giftLog.getGiftId();
        this.sendNum = giftLog.getSendNum();
        this.totalPrice = giftLog.getSendAmount();
        this.liveUserId = receiveUser.getLiveUserId();
        this.liveUserName = receiveUser.getDisplayName();
        this.sendTime = giftLog.getCreateTime();
        this.platformTag = platformTag;
    }

    @Override
    public String toString() {
        return "UserGiftDetailData{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", vipLevel='" + vipLevel + '\'' +
                ", giftOfRoomId='" + giftOfRoomId + '\'' +
                ", giftOfLiveUserId='" + liveUserId + '\'' +
                ", giftId='" + giftId + '\'' +
                ", sendNum=" + sendNum +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
