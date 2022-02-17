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

@Service("BlogsOfDiscoverBlogsRequest")
@Slf4j
public class BlogsOfDiscoverBlogsRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    BlogsListProcess blogsListProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Blog.BlogsOfDiscoverBlogsRequest blogsOfDiscoverBlogsRequest = message.getBlogsOfDiscoverBlogsRequest();
        List<Long> blogsIds = blogsListProcess.randomGetUserBlogsList(userId, blogsOfDiscoverBlogsRequest.getLabelsIdsList());
        List<String> strBlogsIds = blogsIds.stream().map(String::valueOf).collect(Collectors.toList());
        var reply = Blog.BlogsOfDiscoverBlogsReply.newBuilder().addAllBlogsIds(strBlogsIds).build();
        return buildReply(userId, reply, SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
