package com.donglaistd.jinli.database.entity.chat;

import com.donglaistd.jinli.Constant;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class BulletChatMessageRecord {
    @Id
    private String id = ObjectId.get().toString();
    private String msg;
    private long sendTime;
    private String fromUid;
    private String roomId;
    private Constant.PlatformType platform;

    public BulletChatMessageRecord(String msg,String fromUid, String roomId, Constant.PlatformType platform) {
        this.msg = msg;
        this.sendTime = System.currentTimeMillis();
        this.fromUid = fromUid;
        this.roomId = roomId;
        this.platform = platform;
    }

    public BulletChatMessageRecord() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getFromUid() {
        return fromUid;
    }

    public void setFromUid(String fromUid) {
        this.fromUid = fromUid;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Constant.PlatformType getPlatform() {
        return platform;
    }

    public void setPlatform(Constant.PlatformType platform) {
        this.platform = platform;
    }
}
