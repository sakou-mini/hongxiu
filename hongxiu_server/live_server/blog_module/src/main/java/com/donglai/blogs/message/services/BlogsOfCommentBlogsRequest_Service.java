package com.donglai.blogs.message.services;

import com.donglai.blogs.message.producer.Producer;
import com.donglai.blogs.process.BlogsCommentProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.StringUtils;
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
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.blogs.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.BLOGS_COMMENT_NOT_EXIT;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("BlogsOfCommentBlogsRequest")
public class BlogsOfCommentBlogsRequest_Service implements TopicMessageServiceI<String> {

    final BlogsService blogsService;
    final BlogsCommentProcess blogsCommentProcess;
    @Autowired
    BlogsInteractiveService blogsInteractiveService;
    @Autowired
    Producer producer;
    @Autowired
    UserService userService;
    @Autowired
    BlogsCommentService blogsCommentService;

    public BlogsOfCommentBlogsRequest_Service(BlogsService blogsService, BlogsCommentProcess blogsCommentProcess) {
        this.blogsService = blogsService;
        this.blogsCommentProcess = blogsCommentProcess;
    }

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getBlogsOfCommentBlogsRequest();
        var reply = Blog.BlogsOfCommentBlogsReply.newBuilder();
        long blogsId = Long.parseLong(request.getBlogsId());
        User byId = userService.findById(userId);
        var parentCommentId = StringUtils.isNullOrBlank(request.getCommentId()) ? null : Long.parseLong(request.getCommentId());
        //目前只支持2层评论
        parentCommentId = getBlogCommentParentId(parentCommentId);
        var blogs = blogsService.findById(blogsId);
        Constant.ResultCode resultCode = blogsCommentProcess.verifyCommentRequest(byId, request, blogs);
        if (Objects.equals(SUCCESS, resultCode)) {
            Blog.BlogsComment blogsComment = blogsCommentProcess.commentOrReplyBlogs(userId, blogsId, request.getText(), request.getReplyToUserId(), parentCommentId);
            if (Objects.isNull(blogsComment)) {
                resultCode = BLOGS_COMMENT_NOT_EXIT;
            } else {
                resultCode = SUCCESS;
                reply.setBlogsComment(blogsComment).setBlogsId(String.valueOf(blogsId));
                if(StringUtils.isNullOrBlank(blogsComment.getParentCommentId())){
                    User to = userService.findById(blogs.getUserId());
                    if (to.isOnline()) {
                        Blog.BlogsOfInteractiveBroadcastMessage.Builder builder = Blog.BlogsOfInteractiveBroadcastMessage.newBuilder();
                        Blog.Interactive.Builder interactive = Blog.Interactive.newBuilder().setBlogsId(String.valueOf(blogs.getId()))
                                .setFromUserId(byId.getId()).setComment(request.getText())
                                .setInteractive(Constant.InteractiveType.COMMENT);
                        builder.setInteractive(interactive);
                        producer.sendReply(blogs.getUserId(), builder.build(), SUCCESS);
                    } else {
                        BlogsInteractive blogsInteractive = BlogsInteractive.newInstance(blogs.getId(),
                                System.currentTimeMillis(), userId, blogs.getUserId(), blogsComment.getCommentId(),Constant.InteractiveType.COMMENT);
                        blogsInteractiveService.save(blogsInteractive);
                    }
                }
            }
        }
        return buildReply(userId, reply.build(), resultCode);
    }

    @Override
    public void Close(String s) {

    }

    public Long getBlogCommentParentId(Long parentCommentId) {
        if (Objects.isNull(parentCommentId)) return null;
        BlogsComment blogsComment = blogsCommentService.findById(parentCommentId);
        if (Objects.isNull(blogsComment)) return null;
        if (Objects.nonNull(blogsComment.getParentCommentId())) {
            return getBlogCommentParentId(blogsComment.getParentCommentId());
        } else
            return blogsComment.getId();
    }
}
