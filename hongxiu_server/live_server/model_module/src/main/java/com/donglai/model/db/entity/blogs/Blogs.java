package com.donglai.model.db.entity.blogs;

import com.alibaba.fastjson.annotation.JSONField;
import com.donglai.common.annotation.AutoIncKey;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.*;

import static com.donglai.protocol.Constant.BlogsStatus.BLOGS_UNAPPROVED;

@Document
@Data
@NoArgsConstructor
public class Blogs implements Serializable {
    @Id
    @AutoIncKey
    private long id;
    @Field
    @Indexed
    private String userId;
    @Field
    private long createAt;
    @Field
    private long updateTime;
    @Field
    @Indexed
    private String content;
    @Field
    private Constant.BlogsStatus blogsStatus = BLOGS_UNAPPROVED;
    @Field
    private Constant.BlogsType blogsType = Constant.BlogsType.BLOGS_VIDEO;
    @Field
    private List<String> resourceUrl = new ArrayList<>();

    @Field
    private List<String> thumbnails = new ArrayList<>(0);
    @Field
    private int likeNum;
    @Field
    private int commentNum;

    @Field
    private Set<Integer> labels = new HashSet<>();

    @Field
    private long lastTime;

    @Field
    private int failNum;

    @DBRef
    private BlogsMusic blogsMusic;


    @JSONField(serialize = false)
    public String getStringId() {
        return String.valueOf(id);
    }

    public Blogs(String userId, String content, Constant.BlogsStatus blogsStatus, Constant.BlogsType blogsType, List<String> thumbnails) {
        this.userId = userId;
        this.blogsStatus = blogsStatus;
        this.content = content;
        this.blogsType = blogsType;
        this.thumbnails = thumbnails;
        this.createAt = System.currentTimeMillis();
    }

    public static Blogs newInstance(String userId, String content, Constant.BlogsStatus status, Constant.BlogsType blogsType, List<String> thumbnails) {
        return new Blogs(userId, content, status, blogsType, thumbnails);
    }

    public Blog.BlogsInfo toProto() {
        var builder = Blog.BlogsInfo.newBuilder().setBlogsId(String.valueOf(id))
                .setAuthorId(userId).setCreateTime(String.valueOf(createAt))
                .setContent(content).setType(blogsType).addAllResourceUrl(resourceUrl)
                .setLikeNum(String.valueOf(likeNum)).addAllLabelsIds(labels).setStatus(blogsStatus);
        if (blogsMusic != null) builder.setBlogsMusicInfo(blogsMusic.toProto());
        if (!thumbnails.isEmpty() && Objects.equals(Constant.BlogsType.BLOGS_VIDEO, blogsType)) {
            builder.addAllThumbnail(thumbnails);
        }
        return builder.build();
    }

    public void audit(Constant.BlogsStatus blogsStatus) {
        this.blogsStatus = blogsStatus;
        this.updateTime = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Blogs blogs = (Blogs) o;
        return id == blogs.id &&
                userId.equals(blogs.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId);
    }
}
