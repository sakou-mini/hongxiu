package com.donglai.model.db.entity.common.statistics;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
public class UserOperationRecord {
    @Id
    public String userId; //meansUserId
    public long lastPublishBlogsTime;


    public UserOperationRecord(String userId) {
        this.userId = userId;
    }

    public static UserOperationRecord newInstance(String userId) {
        return new UserOperationRecord(userId);
    }
}
