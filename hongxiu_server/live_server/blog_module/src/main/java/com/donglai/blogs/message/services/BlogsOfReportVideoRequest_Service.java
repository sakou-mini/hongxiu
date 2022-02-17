package com.donglai.blogs.message.services;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.live.ReportVideo;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static com.donglai.blogs.util.MessageUtil.buildReply;

/**
 * @author Moon
 * @date 2022-01-22 14:14
 */
@Service("BlogsOfReportVideoRequest")
public class BlogsOfReportVideoRequest_Service implements TopicMessageServiceI<String> {

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Blog.BlogsOfReportVideoRequest request = message.getBlogsOfReportVideoRequest();
        Blog.BlogsOfReportVideoReply.Builder builder = Blog.BlogsOfReportVideoReply.newBuilder();


        if (StringUtils.isEmpty(request.getReason()) || request.getBlogsId() == 0) {
            return buildReply(userId, builder, Constant.ResultCode.MISSING_OR_ILLEGAL_PARAMETERS);
        }
        ReportVideo reportVideo = ReportVideo.newInstance(request.getBlogsId(), request.getReason());
        reportVideo.setCreatedId(userId);

        return buildReply(userId, builder, Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }



}
