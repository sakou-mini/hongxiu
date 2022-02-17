package com.donglai.blogs.message.services;

import com.donglai.blogs.process.BlogsProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.blogs.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.BLOGS_NOT_AUTHOR;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("BlogsOfDeleteBlogsRequest")
public class BlogsOfDeleteBlogsRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    UserService userService;
    @Autowired
    BlogsService blogsService;
    @Autowired
    BlogsProcess blogsProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getBlogsOfDeleteBlogsRequest();
        var reply = Blog.BlogsOfDeleteBlogsReply.newBuilder();
        User user = userService.findById(userId);
        long blogsId = Long.parseLong(request.getBlogsId());
        Blogs blogs = blogsService.findById(blogsId);
        Constant.ResultCode resultCode;
        if (Objects.isNull(blogs)) {
            resultCode = Constant.ResultCode.BLOGS_NOT_EXIT;
        } else if (!Objects.equals(blogs.getUserId(), userId)) {
            resultCode = BLOGS_NOT_AUTHOR;
        } else {
            resultCode = SUCCESS;
            blogsProcess.removeBlogs(blogsId);
        }
        return buildReply(userId, reply, resultCode);
    }

    @Override
    public void Close(String s) {

    }
}
