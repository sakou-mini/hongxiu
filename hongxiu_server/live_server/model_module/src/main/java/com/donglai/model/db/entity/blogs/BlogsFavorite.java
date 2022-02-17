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
public class BlogsFavorite {
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
    private long time;

    private BlogsFavorite(String userId, long blogsId, String blogsAuthor) {
        this.userId = userId;
        this.blogsId = blogsId;
        this.blogsAuthor = blogsAuthor;
        this.time = System.currentTimeMillis();
    }

    public static BlogsFavorite newInstance(String userId, long blogsId, String blogsAuthor) {
        return new BlogsFavorite(userId, blogsId, blogsAuthor);
    }

}
