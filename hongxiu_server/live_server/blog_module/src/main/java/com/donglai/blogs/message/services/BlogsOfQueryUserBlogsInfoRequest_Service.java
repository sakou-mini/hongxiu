package com.donglai.blogs.message.services;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.service.blogs.BlogsFavoriteService;
import com.donglai.model.db.service.blogs.BlogsLikeService;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.model.db.service.common.PersonalSettingService;
import com.donglai.protocol.Blog;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.donglai.blogs.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.BlogsStatus.BLOG_DELETE;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("BlogsOfQueryUserBlogsInfoRequest")
@Slf4j
public class BlogsOfQueryUserBlogsInfoRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    PersonalSettingService personalSettingService;
    @Autowired
    BlogsService blogsService;
    @Autowired
    BlogsLikeService blogsLikeService;
    @Autowired
    BlogsFavoriteService blogsFavoriteService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getBlogsOfQueryUserBlogsInfoRequest();
        var replyBuilder = Blog.BlogsOfQueryUserBlogsInfoReply.newBuilder();
        String requestUserId = request.getUserId();
        //TODO 获赞统计(可优化。从redis处统计)
        long praiseNum = blogsLikeService.countBlogsLikeByAuthor(requestUserId);
        //点赞博客
        // long praiseBlogsIds = blogsLikeService.countLikeBlogsByUserId(requestUserId);
        //收藏博客
        long favoritesBlogsNum = blogsFavoriteService.countBlogsFavoriteNumByUserId(requestUserId);
        //用户发布的动态
        List<String> publishBlogsIds;
        if (Objects.equals(requestUserId, userId)) {
            //本人除了已下架的动态均可见。
            publishBlogsIds = blogsService.findByUserId(requestUserId).stream().filter(blogs -> !Objects.equals(blogs.getBlogsStatus(), BLOG_DELETE)).map(Blogs::getStringId).collect(Collectors.toList());
        } else {
            //访客仅可见已通过动态
            publishBlogsIds = blogsService.findUserPassedBlogs(requestUserId).stream().map(Blogs::getStringId).collect(Collectors.toList());
        }
        replyBuilder.setLikeNum(String.valueOf(praiseNum)).setPublishBlogsNum(String.valueOf(publishBlogsIds.size()))
                .setLikeBlogsNum(String.valueOf(praiseNum)).setFavoritesBlogsNum(String.valueOf(favoritesBlogsNum))
                .addAllPublishBlogs(publishBlogsIds).setUserId(requestUserId);

        return buildReply(userId, replyBuilder.build(), SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
