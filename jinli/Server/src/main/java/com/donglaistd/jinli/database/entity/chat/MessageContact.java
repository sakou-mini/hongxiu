package com.donglaistd.jinli.database.entity.chat;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class MessageContact {
    private ObjectId id = ObjectId.get();
    private String senderId;
    private String receiverId;
    private long time;

    public MessageContact() {
    }

    private MessageContact(String senderId, String receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.time = System.currentTimeMillis();
    }
    public static MessageContact newInstance(String senderId, String receiverId) {
        return new MessageContact(senderId,receiverId);
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

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
