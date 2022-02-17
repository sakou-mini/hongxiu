package com.donglai.blogs.test.process;

import com.donglai.blogs.message.services.BlogsOfCommentBlogsRequest_Service;
import com.donglai.blogs.message.services.BlogsOfDeleteBlogsRequest_Service;
import com.donglai.blogs.message.services.BlogsOfLikeBlogsRequest_Service;
import com.donglai.blogs.message.services.BlogsOfLikeCommentRequest_Service;
import com.donglai.blogs.process.BlogsProcess;
import com.donglai.blogs.test.BaseTest;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.blogs.BlogsCommentService;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Constant;
import com.donglai.protocol.message.KafkaMessage;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

public class BlogsProcessTest extends BaseTest {
    @Autowired
    BlogsProcess blogsProcess;
    @Autowired
    BlogsService blogsService;
    @Autowired
    BlogsCommentService blogsCommentService;
    @Autowired
    BlogsOfLikeBlogsRequest_Service blogsOfLikeBlogsRequest_service;
    @Autowired
    BlogsOfCommentBlogsRequest_Service blogsOfCommentBlogsRequest_service;
    @Autowired
    BlogsOfLikeCommentRequest_Service blogsOfLikeCommentRequest_service;
    @Autowired
    BlogsOfDeleteBlogsRequest_Service blogsOfDeleteBlogsRequest_service;

    @Test
    public void deleteBlogsTest() {
        //1.创建动态
        Blogs blogs = Blogs.newInstance(this.user.getId(), "我的动态", Constant.BlogsStatus.BLOGS_PASS, Constant.BlogsType.BLOGS_VIDEO, new ArrayList<>());
        blogsService.save(blogs);
        //点赞
        var blogsOfLike = Blog.BlogsOfLikeBlogsRequest.newBuilder().setBlogsId(blogs.getStringId());
        KafkaMessage.TopicMessage topicMessage = blogsOfLikeBlogsRequest_service.Process(this.user.getId(), buildMessageRequest(blogsOfLike.build()));
        Assert.assertEquals(topicMessage.getResultCode(), SUCCESS);
        //评论
        Blog.BlogsOfCommentBlogsRequest.Builder commentRequest = Blog.BlogsOfCommentBlogsRequest.newBuilder().setText("不错").setBlogsId(blogs.getStringId());
        topicMessage = blogsOfCommentBlogsRequest_service.Process(this.user.getId(), buildMessageRequest(commentRequest.build()));
        Blog.BlogsOfCommentBlogsReply comment = parseMessage(topicMessage);
        Assert.assertEquals(topicMessage.getResultCode(), SUCCESS);
        //评论回复
        User replier = createUser("52666", "556633");
        String commentId = comment.getBlogsComment().getCommentId();
        commentRequest = Blog.BlogsOfCommentBlogsRequest.newBuilder().setText("我是回复").setBlogsId(blogs.getStringId()).setCommentId(commentId).setReplyToUserId(this.user.getId());
        topicMessage = blogsOfCommentBlogsRequest_service.Process(replier.getId(), buildMessageRequest(commentRequest.build()));
        Assert.assertEquals(topicMessage.getResultCode(), SUCCESS);
        Blog.BlogsOfCommentBlogsReply commentReply = parseMessage(topicMessage);

        //评论的点赞
        Blog.BlogsOfLikeCommentRequest.Builder commentLike = Blog.BlogsOfLikeCommentRequest.newBuilder().setCommentId(commentId);
        topicMessage = blogsOfLikeCommentRequest_service.Process(replier.getId(), buildMessageRequest(commentLike.build()));
        Assert.assertEquals(topicMessage.getResultCode(), SUCCESS);

        //删除动态
        Blog.BlogsOfDeleteBlogsRequest.Builder delBlog = Blog.BlogsOfDeleteBlogsRequest.newBuilder().setBlogsId(blogs.getStringId());
        topicMessage = blogsOfDeleteBlogsRequest_service.Process(user.getId(), buildMessageRequest(delBlog.build()));
        Assert.assertEquals(topicMessage.getResultCode(), SUCCESS);
    }

}
