package com.donglai.blogs.message.services;

import com.donglai.blogs.process.BlogsCommentProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.service.blogs.BlogsCommentService;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.donglai.blogs.util.MessageUtil.buildReply;

@Service("BlogsOfQueryCommentByIdsRequest")
public class BlogsOfQueryCommentByIdsRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    private BlogsCommentService commentService;
    @Autowired
    private BlogsCommentProcess blogsCommentProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getBlogsOfQueryCommentByIdsRequest();
        var replyBuilder = Blog.BlogsOfQueryCommentByIdsReply.newBuilder();
        Blog.BlogsComment comment;
        for (String commentId : request.getCommentIdsList()) {

            comment = blogsCommentProcess.buildBlogsComment(Long.parseLong(commentId), userId);
            if (comment != null) replyBuilder.addBlogsComments(comment);
        }
        return buildReply(userId, replyBuilder.build(), Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
