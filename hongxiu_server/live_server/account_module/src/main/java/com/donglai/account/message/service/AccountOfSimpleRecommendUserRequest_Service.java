package com.donglai.account.message.service;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.account.PrivateChatSession;
import com.donglai.model.db.entity.back.RecommendUser;
import com.donglai.model.db.entity.common.FollowList;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.account.PrivateChatSessionService;
import com.donglai.model.db.service.back.RecommendUserService;
import com.donglai.model.db.service.common.FollowListService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.donglai.account.util.MessageUtil.buildReply;

/**
 * @author Moon
 * @date 2022-01-10 13:42
 */
@Service("AccountOfSimpleRecommendUserRequest")
public class AccountOfSimpleRecommendUserRequest_Service implements TopicMessageServiceI<String> {


    @Autowired
    private FollowListService followListService;
    @Autowired
    private PrivateChatSessionService privateChatSessionService;
    @Autowired
    private RecommendUserService recommendUserService;
    @Autowired
    private UserService userService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Account.AccountOfSimpleRecommendUserRequest request = message.getAccountOfSimpleRecommendUserRequest();
        Account.AccountOfSimpleRecommendUserReply.Builder builder = Account.AccountOfSimpleRecommendUserReply.newBuilder();
        List<String> res = new ArrayList<>();
        //过滤这些用户
        HashSet<String> ids = Sets.newHashSet(request.getUserIdList());
        ids.add(userId);
        //能推荐的用户
        List<String> allRecommendUser = recommendUserService.findAll().stream().map(RecommendUser::getUserId).collect(Collectors.toList());

        List<User> byIds = userService.findByIds(allRecommendUser);
        byIds.addAll(userService.findByTouristIs(false));

        //已经关注过 和  私聊过的
        List<String> leaderIds = followListService.findUserLeaders(userId).stream().map(FollowList::getLeaderId)
                .collect(Collectors.toList());
        List<String> chats = privateChatSessionService.findByFromUid(userId).stream().map(PrivateChatSession::getToUid).collect(Collectors.toList());

        ids.addAll(leaderIds);
        ids.addAll(chats);

        List<User> collect = byIds.stream().filter(v -> !ids.contains(v.getId())).collect(Collectors.toList());
        List<String> userIds = collect.stream().map(User::getId).distinct().collect(Collectors.toList());

        int maxNum = Math.min(userIds.size(), request.getSize());
        builder.addAllUserId(userIds.subList(0, maxNum));
        return buildReply(userId, builder, Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
