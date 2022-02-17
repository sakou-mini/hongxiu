package com.donglai.blogs.message.services;

import com.donglai.blogs.process.BlogsProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.blogs.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.*;

@Service("BlogsOfLikeBlogsRequest")
public class BlogsOfLikeBlogsRequest_Service implements TopicMessageServiceI<String> {
    final BlogsService blogsService;
    final BlogsProcess blogsProcess;

    public BlogsOfLikeBlogsRequest_Service(BlogsService blogsService, BlogsProcess blogsProcess) {
        this.blogsService = blogsService;
        this.blogsProcess = blogsProcess;
    }

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getBlogsOfLikeBlogsRequest();
        var reply = Blog.BlogsOfLikeBlogsReply.getDefaultInstance();
        long blogsId = Long.parseLong(request.getBlogsId());
        var blogs = blogsService.findById(blogsId);
        Constant.ResultCode resultCode;
        if (Objects.isNull(blogs)) {
            resultCode = BLOGS_NOT_EXIT;
        } else if(!Objects.equals(blogs.getBlogsStatus(),Constant.BlogsStatus.BLOGS_PASS)){
            resultCode =  BLOGS_NOT_PASS;
        } else {
            resultCode = SUCCESS;
            reply = blogsProcess.likeOrUnlikeBlogs(userId, blogs);
        }
        return buildReply(userId, reply, resultCode);
    }

    @Override
    public void Close(String s) {

    }
}
