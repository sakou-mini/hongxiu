package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.FollowListDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.service.UserProcess;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.VerifyUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;
@Component
public class QueryLiveUserFansListRequestHandler extends MessageHandler{
    final VerifyUtil verifyUtil;
    final FollowListDaoService followListDaoService;
    final DataManager dataManager;
    final UserProcess userProcess;
    final UserDaoService userDaoService;

    public QueryLiveUserFansListRequestHandler(VerifyUtil verifyUtil, FollowListDaoService followListDaoService, DataManager dataManager, UserProcess userProcess, UserDaoService userDaoService) {
        this.verifyUtil = verifyUtil;
        this.followListDaoService = followListDaoService;
        this.dataManager = dataManager;
        this.userProcess = userProcess;
        this.userDaoService = userDaoService;
    }

    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QueryLiveUserFansListRequest request = messageRequest.getQueryLiveUserFansListRequest();
        Jinli.QueryLiveUserFansListReply.Builder replyBuilder = Jinli.QueryLiveUserFansListReply.newBuilder();
        User queryUser = dataManager.findUser(request.getUserId());
        if(Objects.isNull(queryUser))
            return buildReply(replyBuilder, Constant.ResultCode.USER_NOT_FOUND);
        if(!verifyUtil.checkIsLiveUser(queryUser))
            return buildReply(replyBuilder, Constant.ResultCode.NOT_LIVE_USER);
        List<String> fansIds = followListDaoService.findFolloweeIdsByFollowerId( queryUser.getLiveUserId());
        List<Jinli.UserInfo> fansInfo = fansIds.stream().map(fansId -> userProcess.buildUserInfo(userDaoService.findById(fansId))).collect(Collectors.toList());
        replyBuilder.addAllFansInfo(fansInfo);
        return buildReply(replyBuilder, Constant.ResultCode.SUCCESS);
    }
}
