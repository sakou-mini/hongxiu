package com.donglai.model.db.entity.blogs;

import com.donglai.protocol.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

import static com.donglai.protocol.Constant.BlogsStatus.BLOGS_PASS;
import static com.donglai.protocol.Constant.BlogsStatus.BLOGS_UNAPPROVED;

/**
 * @author Moon
 * @date 2021-11-11 15:32
 */
@Document
@Data
@NoArgsConstructor
public class BlogsReview {
    @Id
    private long id;
    @Field
    @Indexed
    private String userId;
    @Field
    @Indexed
    private String refuseReason;
    @Field
    private String resourceUrl;
    //机审结果
    @Field
    private AuditEnum systemAuditStatus = AuditEnum.AUDIT_EMPTY;
    //人工审核结果
    @Field
    private AuditEnum manualAuditStatus = AuditEnum.AUDIT_EMPTY;
    //审核人员id
    @Field
    private String backofficeUserId;
    @Field
    private long auditTime;
    //动态状态
    @Field
    private Constant.BlogsStatus blogsStatus = BLOGS_UNAPPROVED;

    public void manualAudit(Constant.BlogsStatus status, String backofficeUserId) {
        if (Objects.equals(status, BLOGS_PASS)) {
            this.manualAuditStatus = AuditEnum.AUDIT_PASS;
        } else {
            this.manualAuditStatus = AuditEnum.AUDIT_NOT_PASS;
        }
        this.backofficeUserId = backofficeUserId;
        this.blogsStatus = status;
        this.auditTime = System.currentTimeMillis();
    }

    public void systemAudit(Constant.BlogsStatus status) {
        if (Objects.equals(status, BLOGS_PASS)) {
            this.systemAuditStatus = AuditEnum.AUDIT_PASS;
        } else {
            this.systemAuditStatus = AuditEnum.AUDIT_NOT_PASS;
        }
        this.blogsStatus = status;
        this.auditTime = System.currentTimeMillis();
    }

    public BlogsReview(long id, String userId) {
        this.id = id;
        this.userId = userId;
    }
}
