package com.donglai.blogs.message.services;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.service.blogs.BlogsFavoriteService;
import com.donglai.model.service.UserPermissionService;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.donglai.blogs.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("BlogsOfQueryUserFavoritesBlogsRequest")
@Slf4j
public class BlogsOfQueryUserFavoritesBlogsRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    UserPermissionService userPermissionService;
    @Autowired
    BlogsFavoriteService blogsFavoriteService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getBlogsOfQueryUserFavoritesBlogsRequest();
        var replyBuilder = Blog.BlogsOfQueryUserFavoritesBlogsReply.newBuilder();
        String requestUserId = request.getUserId();
        Constant.ResultCode resultCode;
        resultCode = SUCCESS;
        List<String> blogsIds = blogsFavoriteService.findByUserId(requestUserId).stream().map(likeBlogs -> String.valueOf(likeBlogs.getBlogsId())).collect(Collectors.toList());
        replyBuilder.addAllBlogsIds(blogsIds);
        return buildReply(userId, replyBuilder.build(), resultCode);
    }

    @Override
    public void Close(String s) {

    }
}
