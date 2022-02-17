package com.donglai.blogs.test.message.service;

import com.donglai.blogs.message.services.*;
import com.donglai.blogs.message.services.queue.handler.impl.BlogsReviewHandler;
import com.donglai.blogs.test.BaseTest;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Constant;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.donglai.protocol.Constant.ResultCode.BLOGS_PUBLISH_OVERLIMIT;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Slf4j
public class BlogsMessageServiceTest extends BaseTest {
    @Autowired
    BlogsOfQueryUserBlogsInfoRequest_Service queryUserBlogsInfoRequest_service;

    @Test
    public void queryUserBlogsInfoTest() {
        var request = buildMessageRequest(Blog.BlogsOfQueryUserBlogsInfoRequest.newBuilder().setUserId(user.getId()).build());
        var reply = queryUserBlogsInfoRequest_service.Process(user.getId(), request);
        Blog.BlogsOfQueryUserBlogsInfoReply queryUserBlogsInfoReply = parseMessage(reply);
        assert queryUserBlogsInfoReply != null;
        Assert.assertEquals(1000, queryUserBlogsInfoReply.getPublishBlogsCount());
        log.info("size is {}", queryUserBlogsInfoReply.getSerializedSize());
    }

    @Autowired
    BlogsOfCreateBlogsRequest_Service blogsOfCreateBlogsRequest_service;

    @Test
    public void CreateBlogsRequestTest() {
        var createBlogsParam = Blog.BlogsOfCreateBlogsRequest.newBuilder().setContent("我的博客").addThumbnail("thumbnail")
                .setType(Constant.BlogsType.BLOGS_VIDEO).addResourceUrl("/video");
        var request = buildMessageRequest(createBlogsParam.build());
        var reply = blogsOfCreateBlogsRequest_service.Process(user.getId(), request);
        Assert.assertEquals(BLOGS_PUBLISH_OVERLIMIT, reply.getResultCode());
    }

    @Autowired
    BlogsOfDiscoverBlogsRequest_Service blogsOfDiscoverBlogsRequest_service;

    @Test
    public void DiscoverBlogsRequestTest() {
        var request = buildMessageRequest(Blog.BlogsOfDiscoverBlogsRequest.newBuilder().build());
        var reply = blogsOfDiscoverBlogsRequest_service.Process(user.getId(), request);
        Blog.BlogsOfDiscoverBlogsReply blogsReply = parseMessage(reply);
        assert blogsReply != null;
        Assert.assertFalse(blogsReply.getBlogsIdsList().isEmpty());
    }

    @Autowired
    BlogsOfQueryBlogsDetailRequest_Service queryBlogsDetailRequest_service;

    @Test
    public void QueryBlogsDetailTest() {
        Blog.BlogsOfQueryBlogsDetailRequest.Builder builder = Blog.BlogsOfQueryBlogsDetailRequest.newBuilder();
        for (int i = 1; i <= 100; i++) {
            builder.addBlogsId(i + "");
        }
        var request = buildMessageRequest(builder.build());
        var reply = queryBlogsDetailRequest_service.Process(user.getId(), request);
        Blog.BlogsOfQueryBlogsDetailReply blogsReply = parseMessage(reply);
        assert blogsReply != null;
        Assert.assertEquals(100, blogsReply.getBlogsDetailsCount());
    }

    @Autowired
    BlogsOfLikeBlogsRequest_Service blogsOfLikeBlogsRequest_service;

    @Test
    public void LikeBlogsRequest() {
        var request = buildMessageRequest(Blog.BlogsOfLikeBlogsRequest.newBuilder().setBlogsId("1").build());
        var reply = blogsOfLikeBlogsRequest_service.Process(user.getId(), request);
        Blog.BlogsOfLikeBlogsReply likeReply = parseMessage(reply);
        assert likeReply != null;
        Assert.assertTrue(likeReply.getIsLike());
        Assert.assertEquals(1, Long.parseLong(likeReply.getLikeNum()));


        Blog.BlogsOfQueryBlogsDetailRequest.Builder blogDetailRequest = Blog.BlogsOfQueryBlogsDetailRequest.newBuilder().addBlogsId("1");
        request = buildMessageRequest(blogDetailRequest.build());
        reply = queryBlogsDetailRequest_service.Process(user.getId(), request);
        Blog.BlogsOfQueryBlogsDetailReply blogsReply = parseMessage(reply);
        assert blogsReply != null;
        Assert.assertEquals(1, blogsReply.getBlogsDetailsCount());
        Assert.assertEquals("1", blogsReply.getBlogsDetailsList().get(0).getBlogsInfo().getLikeNum());


        request = buildMessageRequest(Blog.BlogsOfLikeBlogsRequest.newBuilder().setBlogsId("1").build());
        reply = blogsOfLikeBlogsRequest_service.Process(user.getId(), request);
        likeReply = parseMessage(reply);
        assert likeReply != null;
        Assert.assertFalse(likeReply.getIsLike());
        Assert.assertEquals(0, Long.parseLong(likeReply.getLikeNum()));
    }

    @Autowired
    BlogsOfCommentBlogsRequest_Service blogsOfCommentBlogsRequest_service;

    @Test
    public void CommentBlogsRequestTest() {
        var commentRequest = Blog.BlogsOfCommentBlogsRequest.newBuilder().setBlogsId("1").setText("这是我的回复");
        var reply = blogsOfCommentBlogsRequest_service.Process(user.getId(), buildMessageRequest(commentRequest.build()));
        Assert.assertEquals(SUCCESS, reply.getResultCode());
        Blog.BlogsOfCommentBlogsReply comment = parseMessage(reply);
        System.out.println(comment);

        reply = blogsOfCommentBlogsRequest_service.Process(user.getId(), buildMessageRequest(commentRequest.build()));
        Assert.assertEquals(SUCCESS, reply.getResultCode());
    }

    @Autowired
    BlogsService blogsService;
    @Autowired
    BlogsReviewHandler blogsReviewHandler;

    @Test
    public void testReview() throws Exception {
        blogsReviewHandler.deal(null);
    }
}
