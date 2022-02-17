package com.donglai.blogs.message.services;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.donglai.blogs.util.MessageUtil.buildReply;

/**
 * @author Moon
 * @date 2021-11-30 16:46
 * 草稿箱列表
 */
@Service("BlogsOfDraftRequest")
public class BlogsOfDraftRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    private BlogsService blogService;


    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Blog.BlogsOfDraftRequest request = message.getBlogsOfDraftRequest();
        Blog.BlogsOfDraftReply.Builder builder = Blog.BlogsOfDraftReply.newBuilder();
        //查询状态为草稿箱的视频
        List<Blogs> blogs = blogService.findByUserIdInAndBlogsStatusIsOrderByCreateAt(
                Sets.newHashSet(request.getUserId()), Constant.BlogsStatus.BLOGS_DRAFT);
        List<String> collect = blogs.stream().map(Blogs::getStringId).collect(Collectors.toList());
        builder.addAllBlogsIds(collect);
        return buildReply(request.getUserId(), builder, Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
