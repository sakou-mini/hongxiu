package com.donglai.model.db.entity.blogs;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document
@NoArgsConstructor
public class BlogsCommentLike {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    @Indexed
    private String userId;
    @Field
    @Indexed
    private long commentId;
    @Field
    @Indexed
    private long blogsId;
    @Field
    private long time;

    public BlogsCommentLike(String userId, long commentId, long blogsId, long time) {
        this.userId = userId;
        this.commentId = commentId;
        this.time = time;
    }

    public static BlogsCommentLike newInstance(String userId, long commentId, long blogsId) {
        return new BlogsCommentLike(userId, commentId, blogsId, System.currentTimeMillis());
    }
}
