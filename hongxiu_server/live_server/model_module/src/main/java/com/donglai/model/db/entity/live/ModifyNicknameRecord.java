package com.donglai.model.db.entity.live;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
public class ModifyNicknameRecord {
    @Id
    private ObjectId id = ObjectId.get();

    private String userId;

    private long recordTime;

    private String newNickname;


    public ModifyNicknameRecord(String userId, String newNickname) {
        this.userId = userId;
        this.newNickname = newNickname;
        this.recordTime = System.currentTimeMillis();
    }
}
