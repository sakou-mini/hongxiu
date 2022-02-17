package com.donglaistd.jinli.database.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;

@CompoundIndex(def = "{'follower':1,'followee':1}", unique = true)
@Document
public class FollowList {
    @Id
    private ObjectId id = ObjectId.get();

    @DBRef
    @NotNull
    private User follower;

    @DBRef
    @NotNull
    private LiveUser followee;

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getId() {
        return id;
    }

    @Field
    private Date followTime;

    public FollowList(User self, LiveUser liveUser) {
        id = ObjectId.get();
        this.follower = self;
        this.followee = liveUser;
        followTime = Calendar.getInstance().getTime();
    }

    private FollowList() {
    }

    public User getFollower() {
        return follower;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public LiveUser getFollowee() {
        return followee;
    }

    public void setFollowee(LiveUser followee) {
        this.followee = followee;
    }
}
