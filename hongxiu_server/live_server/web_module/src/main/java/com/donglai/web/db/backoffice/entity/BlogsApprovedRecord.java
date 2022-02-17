package com.donglai.web.db.backoffice.entity;

import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.protocol.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
public class BlogsApprovedRecord {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private long approvedTime;
    @Field
    private String backOfficeUserId;
    @Field
    private long blogsId;
    @Field
    private String rejectContent;
    @Field
    private Constant.BlogsStatus status;

    public BlogsApprovedRecord(String backOfficeUserId, long blogsId, Constant.BlogsStatus status) {
        this.backOfficeUserId = backOfficeUserId;
        this.blogsId = blogsId;
        this.status = status;
        this.approvedTime = System.currentTimeMillis();
    }

    public static BlogsApprovedRecord newInstance(String backOfficeUserId, Blogs blogs) {
        return new BlogsApprovedRecord(backOfficeUserId, blogs.getId(), blogs.getBlogsStatus());
    }
}
