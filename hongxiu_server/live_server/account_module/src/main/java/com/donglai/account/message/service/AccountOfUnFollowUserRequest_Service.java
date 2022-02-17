package com.donglai.account.message.service;

import com.donglai.account.process.FollowProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.FollowListService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.NOT_FOLLOWING;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("AccountOfUnFollowUserRequest")
public class AccountOfUnFollowUserRequest_Service implements TopicMessageServiceI<String> {
    final FollowListService followListService;
    final RoomService roomService;
    final FollowProcess followProcess;
    @Autowired
    UserService userService;

    public AccountOfUnFollowUserRequest_Service(FollowListService followListService, RoomService roomService, FollowProcess followProcess) {
        this.followListService = followListService;
        this.roomService = roomService;
        this.followProcess = followProcess;
    }

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfUnFollowUserRequest();
        var reply = Account.AccountOfUnFollowUserReply.newBuilder();
        String leaderId = request.getUserId();
        Constant.ResultCode resultCode;
        if (!followListService.isUserFollower(leaderId, userId)) {
            resultCode = NOT_FOLLOWING;
        } else {
            resultCode = SUCCESS;
            User leader = userService.findById(leaderId);
            User user = userService.findById(userId);
            followListService.deleteFollowListByLeaderIdAndFollowerId(leaderId, userId);
            followProcess.broadcastFollowOrUnFollowByUser(leader, user, System.currentTimeMillis(),Constant.FollowType.UNFOLLOW);
            reply.setLeaderUserId(leaderId);
        }
        return buildReply(userId, reply, resultCode);
    }

    @Override
    public void Close(String s) {

    }
}
