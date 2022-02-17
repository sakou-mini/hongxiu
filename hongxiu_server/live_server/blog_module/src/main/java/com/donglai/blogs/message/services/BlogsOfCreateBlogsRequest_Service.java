package com.donglai.blogs.message.services;

import com.donglai.blogs.process.BlogsProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.blogs.BlogsMusicService;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;

import static com.donglai.blogs.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;
import static com.donglai.protocol.Constant.ResultCode.UNOFFICIAL_USER;

@Service("BlogsOfCreateBlogsRequest")
@Slf4j
public class BlogsOfCreateBlogsRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    private BlogsService blogsService;
    @Autowired
    private BlogsMusicService blogsMusicService;
    @Autowired
    private UserService userService;
    @Autowired
    private BlogsProcess blogsProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getBlogsOfCreateBlogsRequest();
        Blog.BlogsOfCreateBlogsReply.Builder replyBuilder = Blog.BlogsOfCreateBlogsReply.newBuilder();
        User user = userService.findById(userId);
        Constant.ResultCode resultCode = blogsProcess.verifyCreateBlogs(userId, request);
        if (Objects.equals(SUCCESS, resultCode)) {
            if (user.isTourist()) {
                resultCode = UNOFFICIAL_USER;
            } else {
                resultCode = SUCCESS;
                Blogs blogs = Blogs.newInstance(userId, request.getContent(), Constant.BlogsStatus.BLOGS_UNAPPROVED, request.getType(), request.getThumbnailList());
                if (request.getIsDraft()) blogs.setBlogsStatus(Constant.BlogsStatus.BLOGS_DRAFT);
                blogs.setResourceUrl(request.getResourceUrlList());
                blogs.setLabels(new HashSet<>(request.getLabelsList()));
                if (!StringUtils.isNullOrBlank(request.getMusicId())) {
                    blogs.setBlogsMusic(blogsMusicService.findById(Long.parseLong(request.getMusicId())));
                }
                blogsService.save(blogs);
                replyBuilder.setBlogsInfo(blogs.toProto());
                blogsProcess.recordPublishBlogs(userId, blogs);
            }
        }
        return buildReply(userId, replyBuilder.build(), resultCode);
    }


    @Override
    public void Close(String s) {

    }
}
