package com.donglai.web.web.dto.reply;

import com.donglai.model.db.entity.blogs.AuditEnum;
import com.donglai.model.db.entity.blogs.BlogsComment;
import com.donglai.model.db.entity.common.User;
import com.donglai.protocol.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
public class BlogsCommentListReply {
    private long id;
    private String text;
    private CommentUser fromUser;
    private CommentUser toUser;
    private long commentTime;
    private long auditTime;
    private AuditEnum systemAuditStatus; //机审结果
    private AuditEnum manualAuditStatus; //人工审核结果
    private String refuseReason;
    private Constant.CommentStatus status;
    private String operatorName;

    @Data
    public static class CommentUser {
        public String userId;
        public String nickname;

        public CommentUser(User user) {
            this.userId = user.getId();
            this.nickname = user.getNickname();
        }
    }

    public BlogsCommentListReply(BlogsComment blogsComment, User fromUser, User toUser) {
        this.id = blogsComment.getId();
        this.text = blogsComment.getText();
        if (Objects.nonNull(fromUser)) this.fromUser = new CommentUser(fromUser);
        if (Objects.nonNull(toUser)) this.toUser = new CommentUser(toUser);
        this.commentTime = blogsComment.getCommentTime();
        this.auditTime = blogsComment.getAuditTime();
        this.systemAuditStatus = blogsComment.getSystemAuditStatus();
        this.manualAuditStatus = blogsComment.getManualAuditStatus();
        this.refuseReason = blogsComment.getRefuseReason();
        this.status = blogsComment.getStatus();
    }
}
