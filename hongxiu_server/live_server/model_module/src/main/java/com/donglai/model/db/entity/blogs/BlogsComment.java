package com.donglai.model.db.entity.blogs;

import com.alibaba.fastjson.annotation.JSONField;
import com.donglai.common.annotation.AutoIncKey;
import com.donglai.common.util.StringUtils;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

import static com.donglai.protocol.Constant.CommentStatus.COMMENT_PASS;


@Document
@Data
@NoArgsConstructor
public class BlogsComment {
    @Id
    @AutoIncKey
    private long id;
    @Field
    @Indexed
    private long blogsId;
    @Field
    private String text;
    @Field
    private long commentTime;
    @Field
    @Indexed
    private Long parentCommentId;
    @Field
    @Indexed
    String fromUser;
    @Field
    @Indexed
    String toUser;
    @Field
    private long replyNum;  //回复数量,不一定及时，仅用于排序
    @Field
    private long likeNum; //点赞数量,不一定及时，仅用于排序
    @Field
    private AuditEnum systemAuditStatus = AuditEnum.AUDIT_EMPTY; //机审结果
    @Field
    private AuditEnum manualAuditStatus = AuditEnum.AUDIT_EMPTY;  //人工审核结果
    @Field
    private String refuseReason; //拒绝原因
    @Field
    private Constant.CommentStatus status = Constant.CommentStatus.COMMENT_UNAPPROVED;
    @Field
    private String backofficeUserId;    //后天操作人员id
    @Field
    private long auditTime; //审核时间

    public BlogsComment(long blogsId, String text, String fromUser) {
        this.blogsId = blogsId;
        this.text = text;
        this.fromUser = fromUser;
        this.commentTime = System.currentTimeMillis();
    }

    public BlogsComment(Long parentCommentId, long blogsId, String text, String fromUser, String toUser) {
        this.parentCommentId = parentCommentId;
        this.blogsId = blogsId;
        this.text = text;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.commentTime = System.currentTimeMillis();
    }

    public static BlogsComment createComment(long blogsId, String text, String fromUser) {
        return new BlogsComment(blogsId, text, fromUser);
    }

    public static BlogsComment createCommentReply(Long parentCommentId, long blogsId, String text, String fromUser, String toUser) {
        return new BlogsComment(parentCommentId, blogsId, text, fromUser, toUser);
    }

    @JSONField(serialize = false)
    public String getStringId() {
        return String.valueOf(id);
    }

    public void manualAudit(Constant.CommentStatus status, String backofficeUserId) {
        if (Objects.equals(status, COMMENT_PASS)) {
            this.manualAuditStatus = AuditEnum.AUDIT_PASS;
        } else {
            this.manualAuditStatus = AuditEnum.AUDIT_NOT_PASS;
        }
        this.backofficeUserId = backofficeUserId;
        this.status = status;
        this.auditTime = System.currentTimeMillis();
    }

    public void systemAudit(Constant.CommentStatus status) {
        if (Objects.equals(status, COMMENT_PASS)) {
            this.systemAuditStatus = AuditEnum.AUDIT_PASS;
        } else {
            this.systemAuditStatus = AuditEnum.AUDIT_NOT_PASS;
        }
        this.status = status;
        this.auditTime = System.currentTimeMillis();
    }

    public Blog.BlogsComment toProto() {
        Blog.BlogsComment.Builder builder = Blog.BlogsComment.newBuilder().setCommentId(getStringId())
                .setText(text).setText(text)
                .setReplyTime(String.valueOf(commentTime))
                .setFromUserId(fromUser);
        if (!StringUtils.isNullOrBlank(toUser)) builder.setToUserId(toUser);
        if (parentCommentId != null) builder.setParentCommentId(String.valueOf(parentCommentId));
        return builder.build();
    }

}
