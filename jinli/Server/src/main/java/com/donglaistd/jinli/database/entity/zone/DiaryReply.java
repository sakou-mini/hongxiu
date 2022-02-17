package com.donglaistd.jinli.database.entity.zone;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.component.UserSummaryInfo;
import org.apache.logging.log4j.util.Strings;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Document
public class DiaryReply {
    @Id
    private ObjectId id =ObjectId.get();
    @Field
    @Indexed
    private String diaryId;
    @Field
    private String text;
    @Field
    private long replyTime;
    @Field
    private AtomicLong replyNum=new AtomicLong(0);
    @Field
    @Indexed
    private String parentReplyId;
    @Field
    UserSummaryInfo fromUser;
    @Field
    UserSummaryInfo toUser;

    public String getId() {
        return id.toString();
    }

    public String getDiaryId() {
        return diaryId;
    }

    public String getText() {
        return text;
    }

    public String getParentReplyId() {
        return parentReplyId;
    }

    public long getReplyTime() {
        return replyTime;
    }

    public UserSummaryInfo getFromUser() {
        return fromUser;
    }

    public void setFromUser(UserSummaryInfo fromUser) {
        this.fromUser = fromUser;
    }

    public UserSummaryInfo getToUser() {
        return toUser;
    }

    public void setToUser(UserSummaryInfo toUser) {
        this.toUser = toUser;
    }

    public DiaryReply() {
    }

    private DiaryReply(String diaryId, String text, UserSummaryInfo fromUser) {
        this.diaryId = diaryId;
        this.text = text;
        this.fromUser = fromUser;
        this.replyTime = System.currentTimeMillis();
    }

    public static DiaryReply newInstance(String diaryId, String text, UserSummaryInfo fromUser){
        return new DiaryReply(diaryId,text,fromUser);
    }

    public void setLastReply(String parentReplyId, UserSummaryInfo toUser){
        this.parentReplyId = parentReplyId;
        this.toUser = toUser;
    }

    public Jinli.Reply toProto(){
        Jinli.Reply.Builder builder = Jinli.Reply.newBuilder().setReplyTime(this.replyTime).setReplyId(getId()).setText(text).setFromUser(fromUser.toReplyUserInfo())
                .setReplyNum((int) getReplyNum());
        if (Strings.isNotBlank(parentReplyId)) builder.setParentReplyId(parentReplyId);
        if (Objects.nonNull(toUser)) builder.setToUser(toUser.toReplyUserInfo());
        return builder.build();
    }

    public long addReplyNum(){
        return replyNum.incrementAndGet();
    }

    public long getReplyNum(){
        return replyNum.get();
    }
}
