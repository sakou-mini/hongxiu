package com.donglai.blogs.message.services;

import com.donglai.blogs.process.BlogsListProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.protocol.Blog;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.donglai.blogs.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("BlogsOfRecommendationRequest")
@Slf4j
public class BlogsOfRecommendationRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    BlogsListProcess blogsListProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Blog.BlogsOfRecommendationRequest request = message.getBlogsOfRecommendationRequest();
        List<Long> blogIds = blogsListProcess.randomGetUserBlogsList(userId, request.getLabelsIdsList());
        List<String> strBlogsIds = blogIds.stream().map(String::valueOf).collect(Collectors.toList());
        var reply = Blog.BlogsOfRecommendationReply.newBuilder().addAllBlogsId(strBlogsIds).build();
        return buildReply(userId, reply, SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
