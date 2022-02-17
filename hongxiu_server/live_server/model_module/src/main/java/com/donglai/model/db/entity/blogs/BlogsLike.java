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
public class BlogsLike {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    @Indexed
    private String userId;
    @Field
    @Indexed
    private long blogsId;
    @Field
    @Indexed
    private String blogsAuthor;
    @Field
    private boolean tourist;
    @Field
    private long time;

    private BlogsLike(String userId, long blogsId, String blogsAuthor) {
        this.userId = userId;
        this.blogsId = blogsId;
        this.blogsAuthor = blogsAuthor;
        this.time = System.currentTimeMillis();
    }

    public static BlogsLike newInstance(String userId, long blogsId, String blogsAuthor) {
        return new BlogsLike(userId, blogsId, blogsAuthor);
    }
}
