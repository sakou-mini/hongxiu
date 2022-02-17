package com.donglaistd.jinli.database.entity.zone;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class DiaryResource {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    @Indexed
    private String diaryId;
    @Field
    private String resourceUrl;

    private DiaryResource(String diaryId, String resourceUrl) {
        this.diaryId = diaryId;
        this.resourceUrl = resourceUrl;
    }

    public static DiaryResource newInstance(String diaryId, String resourceUrl){
        return new DiaryResource(diaryId, resourceUrl);
    }

    public String getId() {
        return id.toString();
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getDiaryId() {
        return diaryId;
    }

    public void setDiaryId(String diaryId) {
        this.diaryId = diaryId;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

}
