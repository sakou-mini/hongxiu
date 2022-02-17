package com.donglai.live.message.services.message;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.live.process.MessageListProcess;
import com.donglai.model.db.entity.common.FollowList;
import com.donglai.model.db.service.common.FollowListService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Common;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.donglai.live.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("LiveOfMessageListQueryUserLeadersRequest")
public class LiveOfMessageListQueryUserLeadersRequest_Service implements TopicMessageServiceI<String> {
    final FollowListService followListService;
    final MessageListProcess messageListProcess;
    final UserService userService;

    public LiveOfMessageListQueryUserLeadersRequest_Service(FollowListService followListService, MessageListProcess messageListProcess, UserService userService) {
        this.followListService = followListService;
        this.messageListProcess = messageListProcess;
        this.userService = userService;
    }

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var reply = Live.LiveOfMessageListQueryUserLeadersReply.newBuilder();
        List<String> leaders = followListService.findUserLeaders(userId).stream().map(FollowList::getLeaderId).collect(Collectors.toList());
        var messageUserLists = messageListProcess.sortedUserLeaderList(leaders);
        Common.UserInfo userInfo;
        for (MessageListProcess.MessageUserList messageUserList : messageUserLists) {
            userInfo = userService.findById(messageUserList.getUserId()).toSummaryProto().toBuilder()
                    .setIsLive(messageUserList.isLive()).build();
            reply.addUserInfo(userInfo);
        }
        return buildReply(userId, reply.build(), SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
