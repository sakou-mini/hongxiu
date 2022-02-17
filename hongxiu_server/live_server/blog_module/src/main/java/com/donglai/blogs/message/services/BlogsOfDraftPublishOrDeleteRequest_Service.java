package com.donglai.blogs.message.services;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.donglai.blogs.util.MessageUtil.buildReply;

/**
 * @author Moon
 * @date 2021-11-30 17:22
 */
@Service("BlogsOfDraftPublishOrDeleteRequest")
public class BlogsOfDraftPublishOrDeleteRequest_Service implements TopicMessageServiceI<String> {

    @Autowired
    private BlogsService blogService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Blog.BlogsOfDraftPublishOrDeleteRequest request = message.getBlogsOfDraftPublishOrDeleteRequest();
        Blog.BlogsOfDraftPublishOrDeleteReply.Builder builder = Blog.BlogsOfDraftPublishOrDeleteReply.newBuilder();
        String blogsId = request.getBlogsId();
        int publishOrDelete = request.getPublishOrDelete();
        Blogs byId = blogService.findById(Long.parseLong(blogsId));

        switch (publishOrDelete) {
            //发布
            case 0:
                byId.setBlogsStatus(Constant.BlogsStatus.BLOGS_UNAPPROVED);
                blogService.save(byId);
                break;
            //删除
            case 1:
                blogService.deleteById(Long.parseLong(blogsId));
                break;
            default:
                break;
        }
        return buildReply(userId, builder, Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
