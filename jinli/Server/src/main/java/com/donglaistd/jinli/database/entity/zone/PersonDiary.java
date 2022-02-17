package com.donglaistd.jinli.database.entity.zone;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.util.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Objects;

import static com.donglaistd.jinli.Constant.DiaryStatue.DIARY_UPLOADING;

@Document
public class PersonDiary implements Serializable {
    @Id
    private ObjectId id= ObjectId.get();
    @Field
    private String userId;
    @Field
    private long uploadTime;
    @Field
    private Constant.DiaryStatue statue = DIARY_UPLOADING;
    @Field
    private long playNumber;
    @Field
    private boolean commentFlag = false;

    @Field
    private String content;
    @Field
    private int targetNum;
    @Field
    private Constant.DiaryType type;
    @Field
    private String thumbnailUrl;

    public PersonDiary() {
    }

    private PersonDiary(String userId, String content, Constant.DiaryType type, int targetNum) {
        this.userId = userId;
        this.content = content;
        this.type = type;
        this.targetNum = targetNum;
        this.uploadTime = System.currentTimeMillis();
    }

    public static PersonDiary newInstance(String userId, String content, Constant.DiaryType type,int targetNum){
        return new PersonDiary(userId,content,type,targetNum);
    }

    public String getId() {
        return id.toString();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Constant.DiaryType getType() {
        return type;
    }

    public void setType(Constant.DiaryType type) {
        this.type = type;
    }

    public Constant.DiaryStatue getStatue() {
        return statue;
    }

    public void setStatue(Constant.DiaryStatue statue) {
        this.statue = statue;
    }

    public boolean getCommentFlag() {
        return commentFlag;
    }

    public void setCommentFlag(boolean commentFlag) {
        this.commentFlag= commentFlag;
    }

    public long getPlayNumber() {
        return playNumber;
    }

    public void addPlayNumber(){
        this.playNumber++;
    }

    public int getTargetNum() {
        return targetNum;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl == null ? "" : thumbnailUrl;
    }

    public boolean isCommentFlag() {
        return commentFlag;
    }

    public Jinli.Diary toSummaryProto(){
        Jinli.Diary.Builder builder = Jinli.Diary.newBuilder().setId(getId()).setStatue(getStatue()).setUploadTime(getUploadTime()).setType(getType()).setPlayNum(getPlayNumber()).setUserId(getUserId());
        if(!StringUtils.isNullOrBlank(getThumbnailUrl())){
            builder.setThumbnailUrl(getThumbnailUrl());
        }
        return builder.build();
    }

    public Jinli.Diary toProto(){
        return toSummaryProto().toBuilder().setContent(getContent()).setStatue(getStatue()).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonDiary diary = (PersonDiary) o;
        return uploadTime == diary.uploadTime &&
                targetNum == diary.targetNum &&
                id.equals(diary.id) &&
                userId.equals(diary.userId) &&
                type == diary.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, uploadTime, targetNum, type);
    }
}
