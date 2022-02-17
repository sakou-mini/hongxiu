package com.donglai.model.db.entity.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@CompoundIndex(def = "{'leaderId':1,'followerId':1}", unique = true)
@Document
@Data
@NoArgsConstructor
public class FollowList {
    @Id
    private ObjectId id = ObjectId.get();

    @Field
    private String leaderId;

    @Field
    private String followerId;

    @Field
    private long followTime;

    @Field
    private String alias;

    @Field
    private boolean isNew = false;   //TODO 查询个人主页时 设置为false

    public FollowList(String leaderId, String followerId) {
        this.leaderId = leaderId;
        this.followerId = followerId;
        this.followTime = System.currentTimeMillis();
    }

    public static FollowList newInstance(String leaderId, String followerId) {
        return new FollowList(leaderId, followerId);
    }
}
