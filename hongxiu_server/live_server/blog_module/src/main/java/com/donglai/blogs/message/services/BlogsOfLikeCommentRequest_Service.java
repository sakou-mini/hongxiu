package com.donglai.blogs.message.services;

import com.donglai.blogs.process.BlogsCommentProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.service.blogs.BlogsCommentService;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.donglai.blogs.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.BLOGS_COMMENT_NOT_EXIT;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("BlogsOfLikeCommentRequest")
@Slf4j
public class BlogsOfLikeCommentRequest_Service implements TopicMessageServiceI<String> {
    final BlogsCommentProcess blogsCommentProcess;
    final BlogsCommentService blogsCommentService;

    public BlogsOfLikeCommentRequest_Service(BlogsCommentProcess blogsCommentProcess, BlogsCommentService blogsCommentService) {
        this.blogsCommentProcess = blogsCommentProcess;
        this.blogsCommentService = blogsCommentService;
    }

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getBlogsOfLikeCommentRequest();
        var reply = Blog.BlogsOfLikeCommentReply.newBuilder().build();
        long commentId = Long.parseLong(request.getCommentId());
        var comment = blogsCommentService.findById(commentId);
        Constant.ResultCode resultCode;
        if (comment == null) {
            resultCode = BLOGS_COMMENT_NOT_EXIT;
        } else {
            resultCode = SUCCESS;
            reply = blogsCommentProcess.likeOrUnLikeComment(userId, comment);
        }
        return buildReply(userId, reply, resultCode);
    }

    @Override
    public void Close(String s) {

    }
}
