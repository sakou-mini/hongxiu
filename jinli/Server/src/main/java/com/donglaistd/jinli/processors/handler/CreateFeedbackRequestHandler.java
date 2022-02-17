package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.FeedbackDaoService;
import com.donglaistd.jinli.database.dao.backoffice.UserDataStatisticsDaoService;
import com.donglaistd.jinli.database.entity.Feedback;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.backoffice.UserDataStatistics;
import com.donglaistd.jinli.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.FEEDBACK_MESSAGE_FORMAT_ERROR;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class CreateFeedbackRequestHandler extends MessageHandler{
    @Value("${feedback.min.length}")
    private int MIN_FEEDBACK_LENGTH;

    @Value("${feedback.max.length}")
    private int MAX_FEEDBACK_LENGTH;

    @Autowired
    FeedbackDaoService feedbackDaoService;
    @Autowired
    UserDataStatisticsDaoService userDataStatisticsDaoService;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.CreateFeedbackRequest feedbackRequest = messageRequest.getCreateFeedbackRequest();
        Jinli.CreateFeedbackReply.Builder replyBuilder = Jinli.CreateFeedbackReply.newBuilder();
        Constant.ResultCode resultCode = verifyFeedbackRequest(feedbackRequest);
        if(Objects.equals(resultCode,SUCCESS)){
            Feedback feedback = Feedback.newInstance(user.getId(), feedbackRequest.getFeedbackContent());
            UserDataStatistics userRecentLoginInfo = userDataStatisticsDaoService.findUserRecentLoginInfo(user.getId());
            feedback.setBrand(userRecentLoginInfo.getBrand());
            feedback.setMobileMobileModel(userRecentLoginInfo.getMobileModel());
            feedback.setAppVersion(userRecentLoginInfo.getAppVersion());
            feedbackDaoService.save(feedback);
        }
        return buildReply(replyBuilder, resultCode);
    }

    public Constant.ResultCode verifyFeedbackRequest(Jinli.CreateFeedbackRequest request){
        String feedbackContent = request.getFeedbackContent();
        if(StringUtils.isNullOrBlank(feedbackContent) || feedbackContent.length() < MIN_FEEDBACK_LENGTH
                || feedbackContent.length() > MAX_FEEDBACK_LENGTH)
        {
            return FEEDBACK_MESSAGE_FORMAT_ERROR;
        }
        return SUCCESS;
    }
}
