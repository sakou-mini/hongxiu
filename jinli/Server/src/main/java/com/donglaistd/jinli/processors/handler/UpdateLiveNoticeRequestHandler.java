package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class UpdateLiveNoticeRequestHandler extends MessageHandler{
    @Autowired
    DataManager dataManager;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.UpdateLiveNoticeReply.Builder replyBuilder = Jinli.UpdateLiveNoticeReply.newBuilder();
        Jinli.UpdateLiveNoticeRequest request = messageRequest.getUpdateLiveNoticeRequest();
        LiveUser liveUser = dataManager.findLiveUser(user.getLiveUserId());
        if(StringUtils.isNullOrBlank(user.getLiveUserId()))
            return buildReply(replyBuilder, Constant.ResultCode.NOT_LIVE_USER);
        if(liveUser == null)
            return buildReply(replyBuilder, Constant.ResultCode.NOT_LIVE_USER);
        liveUser.setLiveNotice(request.getLiveNotice());
        dataManager.saveLiveUser(liveUser);
        replyBuilder.setLiveNotice(liveUser.getLiveNotice());
        return buildReply(replyBuilder, Constant.ResultCode.SUCCESS);
    }
}
