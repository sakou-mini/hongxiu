package com.donglai.account.message.service.chat;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.account.PrivateChatInBlack;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.account.PrivateChatInBlackService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.service.impl.es.UserElasticsearchServiceImpl;
import com.donglai.protocol.Account;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

/**
 * @author Moon
 * @date 2022-01-07 10:32
 */
@Service("AccountOfBlackListRequest")
public class AccountOfBlackListRequest_Service implements TopicMessageServiceI<String> {

    @Autowired
    private PrivateChatInBlackService privateChatInBlackService;

    @Autowired
    private UserService userService;
    @Autowired
    UserElasticsearchServiceImpl userElasticsearchService;


    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfBlackListRequest();
        var replyBuilder = Account.AccountOfBlackListReply.newBuilder();
        List<PrivateChatInBlack> blackMenuByUserId = privateChatInBlackService.findBlackMenuByUserId(userId, false);
        List<String> blackList = blackMenuByUserId.stream().map(PrivateChatInBlack::getBlackUserId).collect(Collectors.toList());
        if(!StringUtils.isNullOrBlank(request.getName())){
            Map<String, String> queryParam = new HashMap<>();
            queryParam.put("nickname", request.getName());
            List<String> userIds = userElasticsearchService.searchFuzzyQuery(queryParam).stream().map(User::getId).collect(Collectors.toList());
            blackList = blackList.stream().filter(userIds::contains).collect(Collectors.toList());
            replyBuilder.setName(request.getName());
        }
        List<User> blackUserList = userService.findByIds(blackList);

        for (User blackUser : blackUserList) {
            replyBuilder.addUserInfo(blackUser.toSummaryProto());
        }

        return buildReply(userId, replyBuilder, SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
