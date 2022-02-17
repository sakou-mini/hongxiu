package com.donglai.blogs.test.message.service;

import com.donglai.blogs.message.services.BlogsOfSearchRequest_Service;
import com.donglai.blogs.test.BaseTest;
import com.donglai.protocol.Blog;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BlogsOfSearchRequestTest extends BaseTest {
    @Autowired
    BlogsOfSearchRequest_Service blogsOfSearchRequest_service;

    @Test
    public void searchTest() {
        Blog.BlogsOfSearchRequest.Builder builder = Blog.BlogsOfSearchRequest.newBuilder().setKeyword("官方");
        HongXiu.HongXiuMessageRequest.Builder request = HongXiu.HongXiuMessageRequest.newBuilder().setBlogsOfSearchRequest(builder);
        KafkaMessage.TopicMessage reply = blogsOfSearchRequest_service.Process(user.getId(), request.build());
        Blog.BlogsOfSearchReply searchReply = parseMessage(reply);
        assert searchReply != null;
        System.out.println(searchReply.getBlogsIdsCount());
        Assert.assertEquals(10, searchReply.getBlogsIdsCount());
    }
}
