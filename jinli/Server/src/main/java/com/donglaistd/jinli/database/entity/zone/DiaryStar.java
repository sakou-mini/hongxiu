package com.donglaistd.jinli.database.entity.zone;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class DiaryStar {
    @Id
    private ObjectId id = ObjectId.get();

    @Indexed
    @Field
    private String userId;

    @Indexed
    @Field
    private String diaryId;

    public String getId() {
        return id.toString();
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDiaryId() {
        return diaryId;
    }

    public void setDiaryId(String diaryId) {
        this.diaryId = diaryId;
    }

    private DiaryStar(String diaryId, String userId) {
        this.userId = userId;
        this.diaryId = diaryId;
    }
    public static DiaryStar newInstance(String diaryId, String userId){
        return new DiaryStar(diaryId, userId);
    }
}
