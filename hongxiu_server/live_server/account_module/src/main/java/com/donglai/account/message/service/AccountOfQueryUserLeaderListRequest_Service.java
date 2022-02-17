package com.donglai.account.message.service;

import com.donglai.account.process.FollowProcess;
import com.donglai.account.process.UserProcess;
import com.donglai.common.constant.UserPermissionSettingType;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.common.FollowList;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.FollowListService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.service.UserPermissionService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.*;
/*
查询关注列表
* */
@Service("AccountOfQueryUserLeaderListRequest")
@Slf4j
public class AccountOfQueryUserLeaderListRequest_Service implements TopicMessageServiceI<String> {
    final UserService userService;
    final FollowListService followListService;
    final UserProcess userProcess;
    final UserPermissionService userPermissionService;

    public AccountOfQueryUserLeaderListRequest_Service(UserService userService, FollowListService followListService, UserProcess userProcess, UserPermissionService userPermissionService) {
        this.userService = userService;
        this.followListService = followListService;
        this.userProcess = userProcess;
        this.userPermissionService = userPermissionService;
    }

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfQueryUserLeaderListRequest();
        var replyBuilder = Account.AccountOfQueryUserLeaderListReply.newBuilder();
        String queryUserId = request.getUserId();
        String name = request.getName() == null ? "" : request.getName().replaceAll(" ", "");
        Constant.ResultCode resultCode;
        User user = userService.findById(userId);
        if (StringUtils.isNullOrBlank(queryUserId)) {
            resultCode = MISSING_OR_ILLEGAL_PARAMETERS;
        } else if (!userPermissionService.permissionAllow(UserPermissionSettingType.PER_SHOW_FANS_LEADER_LIST, queryUserId, userId)) {
            resultCode = NO_PERMISSION;
        } else {
            if (Objects.isNull(userService.findById(queryUserId))) {
                resultCode = USER_NOT_FOUND;
            } else {
                resultCode = SUCCESS;
                //根据id 和 博主昵称查询
                List<FollowList> followLists = followListService.findByFollowerIdAndLeaderName(queryUserId, name);
                User leader;
                for (FollowList followList : followLists) {
                    leader = userService.findById(followList.getLeaderId());
                    if (Objects.isNull(leader)) {
                        log.warn("not found user by id:-->{}", followList.getLeaderId());
                        continue;
                    }
                    replyBuilder.addFollowInfo(FollowProcess.buildFollowInfo(followList));
                }
            }
        }
        replyBuilder.setUserId(queryUserId).setName(request.getName());
        return buildReply(userId, replyBuilder.build(), resultCode);
    }

    @Override
    public void Close(String s) {

    }
}
