package com.donglai.blogs.message.services;

import com.donglai.blogs.process.BlogsProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.protocol.Blog;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static com.donglai.blogs.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("BlogsOfQueryBlogsDetailRequest")
public class BlogsOfQueryBlogsDetailRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    BlogsProcess blogsProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getBlogsOfQueryBlogsDetailRequest();
        var replayBuilder = Blog.BlogsOfQueryBlogsDetailReply.newBuilder();
        Set<Long> blogsIds = request.getBlogsIdList().stream().map(Long::parseLong).collect(Collectors.toSet());
        blogsIds.forEach(blogsId -> replayBuilder.addBlogsDetails(blogsProcess.queryBlogInfoDetail(blogsId, userId)));
        return buildReply(userId, replayBuilder.build(), SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
