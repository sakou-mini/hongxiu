package com.donglai.account.message.service;

import com.donglai.account.process.FollowProcess;
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
* 查询用户的粉丝列表
* */
@Service("AccountOfQueryUserFollowerListRequest")
@Slf4j
public class AccountOfQueryUserFollowerListRequest_Service implements TopicMessageServiceI<String> {
    private final UserService userService;
    private final FollowListService followListService;
    private final UserPermissionService userPermissionService;

    public AccountOfQueryUserFollowerListRequest_Service(UserService userService, FollowListService followListService, UserPermissionService userPermissionService) {
        this.userService = userService;
        this.followListService = followListService;
        this.userPermissionService = userPermissionService;
    }

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfQueryUserFollowerListRequest();
        var replyBuilder = Account.AccountOfQueryUserFollowerListReply.newBuilder();
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
                //所有粉丝
                List<FollowList> followLists = followListService.findUserFollowerAndFollowerName(queryUserId,name);
                //users.sort(new PinyinComparator());
                User follower;
                for (FollowList followList : followLists) {
                    follower = userService.findById(followList.getFollowerId());
                    replyBuilder.addFollowInfo(FollowProcess.buildFollowInfo(user, follower, followList.getFollowTime(), followList.getAlias()));
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
