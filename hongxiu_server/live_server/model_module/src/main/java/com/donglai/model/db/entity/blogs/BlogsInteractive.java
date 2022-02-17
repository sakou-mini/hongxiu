package com.donglai.model.db.entity.blogs;

import com.donglai.common.annotation.AutoIncKey;
import com.donglai.protocol.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

/**
 * @author Moon
 * @date 2022-01-10 15:54
 */
@Data
@NoArgsConstructor
public class BlogsInteractive {


    @Id
    @AutoIncKey
    private long id;

    private String fromId;

    private String toId;

    private String commentId;

    private long blogsId;

    private Constant.InteractiveType interactiveType;

    private long createdTime;

    public static BlogsInteractive newInstance(long blogsId, long time, String fromId, String toId, String commentId,Constant.InteractiveType type){
        BlogsInteractive blogsInteractive = new BlogsInteractive();
        blogsInteractive.setBlogsId(blogsId);
        blogsInteractive.setCreatedTime(time);
        blogsInteractive.setFromId(fromId);
        blogsInteractive.setCommentId(commentId);
        blogsInteractive.setToId(toId);
        blogsInteractive.setInteractiveType(type);
        return blogsInteractive;
    }
}
