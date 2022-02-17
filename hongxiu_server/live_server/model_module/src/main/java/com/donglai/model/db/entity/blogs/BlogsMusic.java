package com.donglai.model.db.entity.blogs;

import com.donglai.common.annotation.AutoIncKey;
import com.donglai.common.util.StringUtils;
import com.donglai.protocol.Blog;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Moon
 * @date 2021-11-30 14:53
 */
@Document
@Data
@NoArgsConstructor
public class BlogsMusic {
    /**
     * 主键ID
     */
    @Id
    @AutoIncKey
    private long id;

    /**
     * 音乐名称
     */
    @Field
    private String musicName;

    /**
     * 音乐链接
     */
    @Field
    private String musicUrl;

    /**
     * 音乐作者
     */
    @Field
    private String musicAuthor;

    /**
     * 音乐封面
     */
    @Field
    private String musicCoverUrl;

    /**
     * 是否禁用
     */
    @Field
    private boolean status;

    /**
     * 音乐时长
     */
    @Field
    private int duration;

    /**
     * 创建时间
     */
    @Field
    private long createTime;
    /**
     * 更新时间（修改时间）
     */
    @Field
    private long updateTime;
    /**
     * 后台操作人id
     */
    private String backofficeUserId;


    public void auditMusic(boolean status, String backofficeUserId) {
        this.status = status;
        this.backofficeUserId = backofficeUserId;
        this.updateTime = System.currentTimeMillis();
    }

    public BlogsMusic(String musicName, String musicUrl, String musicAuthor, String musicCoverUrl, boolean status, int duration) {
        this.musicName = musicName;
        this.musicUrl = musicUrl;
        this.musicAuthor = musicAuthor;
        this.musicCoverUrl = musicCoverUrl;
        this.status = status;
        this.duration = duration;
        this.createTime = System.currentTimeMillis();
        this.updateTime = this.createTime;
    }

    public static BlogsMusic newInstance(String musicName, String musicUrl, String musicAuthor, String musicCoverUrl, boolean status, Integer duration) {
        return new BlogsMusic(musicName, musicUrl, musicAuthor, musicCoverUrl, status, duration);
    }

    public Blog.BlogsMusicInfo toProto() {
        var builder = Blog.BlogsMusicInfo.newBuilder()
                .setId(String.valueOf(id)).setMusicAuthor(musicAuthor)
                .setMusicName(musicName).setMusicUrl(musicUrl)
                .setDuration(duration);
        if (!StringUtils.isNullOrBlank(musicCoverUrl)) {
            builder.setMusicCoverUrl(musicCoverUrl);
        }
        return builder.build();
    }
}
