package com.donglai.blogs.message.services;

import com.donglai.common.constant.UserPermissionSettingType;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.service.blogs.BlogsLikeService;
import com.donglai.model.service.UserPermissionService;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.donglai.blogs.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.NO_PERMISSION;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("BlogsOfQueryUserLikeBlogsRequest")
public class BlogsOfQueryUserLikeBlogsRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    UserPermissionService userPermissionService;
    @Autowired
    BlogsLikeService blogsLikeService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getBlogsOfQueryUserLikeBlogsRequest();
        var replyBuilder = Blog.BlogsOfQueryUserLikeBlogsReply.newBuilder();
        String requestUserId = request.getUserId();
        Constant.ResultCode resultCode;
        if (!userPermissionService.permissionAllow(UserPermissionSettingType.PER_SHOW_BLOGS_PRAISE, requestUserId, userId)) {
            resultCode = NO_PERMISSION;
        } else {
            resultCode = SUCCESS;
            List<String> likeBlogsIds = blogsLikeService.findLikeBlogsByUserId(requestUserId).stream().map(likeBlogs -> String.valueOf(likeBlogs.getBlogsId())).collect(Collectors.toList());
            replyBuilder.addAllBlogsIds(likeBlogsIds);
        }
        return buildReply(userId, replyBuilder.build(), resultCode);
    }

    @Override
    public void Close(String s) {

    }
}
