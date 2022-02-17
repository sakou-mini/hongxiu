package com.donglai.account.message.service;

import com.donglai.account.process.FollowProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.common.FollowList;
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

import java.util.Objects;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.*;

@Service("AccountOfFollowUserRequest")
public class AccountOfFollowUserRequest_Service implements TopicMessageServiceI<String> {
    final UserService userService;
    final FollowListService followListService;
    final RoomService roomService;
    @Autowired
    FollowProcess followProcess;


    public AccountOfFollowUserRequest_Service(UserService userService, FollowListService followListService, RoomService roomService) {
        this.userService = userService;
        this.followListService = followListService;
        this.roomService = roomService;
    }

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfFollowUserRequest();
        String leaderId = request.getUserId();
        User leader = userService.findById(leaderId);
        var replyBuilder = Account.AccountOfFollowUserReply.newBuilder();
        User user = userService.findById(userId);
        Constant.ResultCode resultCode;
        if (user.isTourist()) {
            resultCode = UNOFFICIAL_USER;
        } else if (Objects.equals(userId, leaderId)) {
            resultCode = CAN_NOT_FOLLOWING_YOURSELF;
        } else if (Objects.isNull(leader)) {
            resultCode = USER_NOT_FOUND;
        } else if (followListService.isUserFollower(leaderId, userId)) {
            resultCode = ALREADY_FOLLOWING;
        } else {
            resultCode = SUCCESS;
            FollowList followList = FollowList.newInstance(leaderId, userId);
            followList = followListService.save(followList);
            followProcess.broadcastFollowOrUnFollowByUser(leader,  user, followList.getFollowTime(), Constant.FollowType.FOLLOW);
            replyBuilder.setLeaderUserId(leaderId);
        }
        return buildReply(userId, replyBuilder.build(), resultCode);
    }


    @Override
    public void Close(String s) {

    }
}
