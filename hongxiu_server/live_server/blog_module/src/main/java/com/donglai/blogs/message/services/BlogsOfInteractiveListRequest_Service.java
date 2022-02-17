package com.donglai.blogs.message.services;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.blogs.BlogsComment;
import com.donglai.model.db.entity.blogs.BlogsInteractive;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.blogs.BlogsCommentService;
import com.donglai.model.db.service.blogs.BlogsInteractiveService;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.donglai.blogs.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

/**
 * @author Moon
 * @date 2022-01-11 10:28
 */
@Service("BlogsOfInteractiveListRequest")
public class BlogsOfInteractiveListRequest_Service implements TopicMessageServiceI<String> {

    @Autowired
    private UserService userService;

    @Autowired
    private BlogsService blogsService;
    @Autowired
    private BlogsCommentService blogsCommentService;
    @Autowired
    private BlogsInteractiveService blogsInteractiveService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Blog.BlogsOfInteractiveListRequest request = message.getBlogsOfInteractiveListRequest();
        Blog.BlogsOfInteractiveListReply.Builder builder = Blog.BlogsOfInteractiveListReply.newBuilder();
        List<BlogsInteractive> byToIdAndPagination = blogsInteractiveService.findByToIdAndPagination(userId, request);
        byToIdAndPagination.forEach(v -> {
            Blog.Interactive interactive = toInteractiveBuilder(v);
            builder.addInteractive(interactive);
        });
        blogsInteractiveService.deleteAll(byToIdAndPagination);
        return buildReply(userId, builder, SUCCESS);

    }

    @Override
    public void Close(String s) {

    }

    public Blog.Interactive toInteractiveBuilder(BlogsInteractive blogsInteractive) {
        Blogs blogs = blogsService.findById(blogsInteractive.getBlogsId());
        User byId = userService.findById(blogsInteractive.getFromId());
        if (blogsInteractive.getInteractiveType().equals(Constant.InteractiveType.COMMENT)) {
            BlogsComment comment = blogsCommentService.findById(Long.parseLong(blogsInteractive.getCommentId()));
            Blog.Interactive.Builder builder = Blog.Interactive.newBuilder();
            builder.setBlogsId(String.valueOf(blogs.getId()));
            builder.setFromUserId(blogsInteractive.getFromId());
            builder.setComment(comment.getText());
            //builder.setCreateTime(String.valueOf(blogsInteractive.getCreatedTime()));
            builder.setInteractive(Constant.InteractiveType.COMMENT);
            return builder.build();
        }
        //只有点赞需要直接发送点赞时间
        Blog.Interactive.Builder builder = Blog.Interactive.newBuilder();
        builder.setBlogsId(String.valueOf(blogs.getId()));
        builder.setFromUserId(blogsInteractive.getFromId());
        builder.setCreateTime(String.valueOf(blogsInteractive.getCreatedTime()));
        builder.setInteractive(Constant.InteractiveType.LIKE);
        return builder.build();
    }
}
