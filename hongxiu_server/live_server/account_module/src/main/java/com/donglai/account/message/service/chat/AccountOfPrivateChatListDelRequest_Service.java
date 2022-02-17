package com.donglai.account.message.service.chat;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.account.PrivateChatSession;
import com.donglai.model.db.service.account.PrivateChatSessionMessageService;
import com.donglai.model.db.service.account.PrivateChatSessionService;
import com.donglai.protocol.Account;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.MISSING_OR_ILLEGAL_PARAMETERS;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

/**
 * @author Moon
 * @date 2021-11-01 17:26
 */
@Service("AccountOfPrivateChatListDelRequest")
public class AccountOfPrivateChatListDelRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    private PrivateChatSessionService privateChatSessionService;

    @Autowired
    private PrivateChatSessionMessageService privateChatSessionMessageService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfPrivateChatListDelRequest();
        String sessionId = request.getSessionId();
        //校验参数
        if (Long.parseLong(sessionId) == 0) {
            return buildReply(userId, Account.AccountOfPrivateChatListDelReply.newBuilder(), MISSING_OR_ILLEGAL_PARAMETERS);
        }
        //查询会话实体
        PrivateChatSession privateChatSession = privateChatSessionService.findById(Long.parseLong(sessionId));
        //是否存在
        if (!Objects.isNull(privateChatSession)) {
            privateChatSession.setDel(true);
            //绘画逻辑删除
            privateChatSessionService.save(privateChatSession);
            //中间表删除聊天记录
            privateChatSessionMessageService.delMessage(Long.parseLong(sessionId));
        }

        return buildReply(userId, Account.AccountOfPrivateChatListDelReply.newBuilder().setSessionId(sessionId), SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
