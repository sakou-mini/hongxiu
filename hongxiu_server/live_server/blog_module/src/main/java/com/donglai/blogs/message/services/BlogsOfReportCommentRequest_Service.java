package com.donglai.blogs.message.services;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.ReportComment;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.ReportCommentService;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.donglai.blogs.util.MessageUtil.buildReply;

/**
 * @author Moon
 * @date 2022-01-22 13:54
 */
@Service("BlogsOfReportCommentRequest")
public class BlogsOfReportCommentRequest_Service implements TopicMessageServiceI<String> {

    @Autowired
    private UserService userService;
    @Autowired
    private ReportCommentService reportCommentService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {

        Blog.BlogsOfReportCommentRequest request = message.getBlogsOfReportCommentRequest();
        Blog.BlogsOfReportCommentReply.Builder builder = Blog.BlogsOfReportCommentReply.newBuilder();
        if (StringUtils.isEmpty(request.getReason()) || request.getCommentId() == 0) {
            return buildReply(userId, builder, Constant.ResultCode.MISSING_OR_ILLEGAL_PARAMETERS);
        }

        ReportComment reportComment = ReportComment.newInstance(request.getReason(), request.getCommentId());
        reportComment.setCreatedId(userId);
        reportCommentService.save(reportComment);
        return buildReply(userId, builder, Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
