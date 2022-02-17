package com.donglaistd.jinli.database.entity.chat;

import com.donglaistd.jinli.Jinli;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "message")
public class MessageRecord {
    @Id
    private ObjectId id = ObjectId.get();

    @Field
    private String msg;

    @Field("send_time")
    private long sendTime;

    @Indexed
    @Field
    private String  fromUid;

    @Indexed
    @Field
    private String toUid;

    private boolean read = true;

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

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    public String getId() {
        return id.toString();
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public static MessageRecord newInstance(String msg, String fromUid, String toUid){
        MessageRecord messageRecord = new MessageRecord();
        messageRecord.setMsg(msg);
        messageRecord.setFromUid(fromUid);
        messageRecord.setToUid(toUid);
        messageRecord.setSendTime(System.currentTimeMillis());
        return messageRecord;
    }

    public static MessageRecord newInstance(String msg, String fromUid){
        return newInstance(msg,fromUid,"");
    }

    public Jinli.MessageRecord toProto(){
        return Jinli.MessageRecord.newBuilder().setContent(this.msg).setFromUid(fromUid).setToUid(toUid).setSendTime(sendTime)
                .setMessageId(getId()).build();
    }

    @Override
    public String toString() {
        return "MessageRecord{" +
                "id=" + id +
                ", msg='" + msg + '\'' +
                ", sendTime=" + sendTime +
                ", fromUid='" + fromUid + '\'' +
                ", toUid='" + toUid + '\'' +
                '}';
    }
}