package com.donglaistd.jinli.database.entity.invite;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class UserInviteRecord {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private String inviteUserId;
    @Field
    private String beInviteUserId;
    @Field
    private long time;

    public String getId() {
        return id.toString();
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getInviteUserId() {
        return inviteUserId;
    }

    public void setInviteUserId(String inviteUserId) {
        this.inviteUserId = inviteUserId;
    }

    public String getBeInviteUserId() {
        return beInviteUserId;
    }

    public void setBeInviteUserId(String beInviteUserId) {
        this.beInviteUserId = beInviteUserId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    private UserInviteRecord(String inviteUserId, String beInviteUserId) {
        this.inviteUserId = inviteUserId;
        this.beInviteUserId = beInviteUserId;
        this.time = System.currentTimeMillis();
    }

    public UserInviteRecord() {
    }

    public static UserInviteRecord newInstance(String inviteUserId, String beInviteUserId){
        return new UserInviteRecord(inviteUserId, beInviteUserId);
    }
}
