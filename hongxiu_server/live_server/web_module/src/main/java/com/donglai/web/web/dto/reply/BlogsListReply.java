package com.donglai.web.web.dto.reply;

import com.donglai.model.db.entity.blogs.AuditEnum;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.blogs.BlogsReview;
import com.donglai.protocol.Constant;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BlogsListReply implements Serializable {
    public long id;
    public List<String> resource;
    public String content;
    public String nickname;
    public String userId;
    public long createTime;
    public long updateTime;
    public AuditEnum systemAuditStatus;
    public AuditEnum manualAuditStatus;
    public Constant.BlogsStatus blogsStatus;
    public Constant.BlogsType blogsType;
    public String operatorName;
    public String refuseReason;
    public List<String> labels;

    public BlogsListReply(Blogs blogs, BlogsReview blogsReview, List<String> labels) {
        this.id = blogs.getId();
        this.resource = blogs.getResourceUrl();
        this.content = blogs.getContent();
        this.userId = blogs.getUserId();
        this.createTime = blogs.getCreateAt();
        this.updateTime = blogs.getUpdateTime();
        this.systemAuditStatus = blogsReview.getSystemAuditStatus();
        this.manualAuditStatus = blogsReview.getManualAuditStatus();
        this.blogsStatus = blogs.getBlogsStatus();
        this.blogsType = blogs.getBlogsType();
        this.refuseReason = blogsReview.getRefuseReason();
        this.labels = labels;
    }
}

