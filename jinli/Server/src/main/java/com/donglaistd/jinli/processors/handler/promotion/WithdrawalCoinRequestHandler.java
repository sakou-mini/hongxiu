package com.donglaistd.jinli.processors.handler.promotion;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.invite.UserAgentDaoService;
import com.donglaistd.jinli.database.dao.invite.UserInviteRecordDaoService;
import com.donglaistd.jinli.database.dao.invite.WithdrawalRecordDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.invite.UserAgent;
import com.donglaistd.jinli.database.entity.invite.WithdrawalRecord;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.service.EventPublisher;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.Constant.ResultCode.NOT_ENOUGH_GAMECOIN;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class WithdrawalCoinRequestHandler extends MessageHandler {
    @Autowired
    UserAgentDaoService userAgentDaoService;
    @Autowired
    UserInviteRecordDaoService userInviteRecordDaoService;
    @Autowired
    WithdrawalRecordDaoService withdrawalRecordDaoService;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.WithdrawalCoinRequest request = messageRequest.getWithdrawalCoinRequest();
        Jinli.WithdrawalCoinReply.Builder reply = Jinli.WithdrawalCoinReply.newBuilder();
        UserAgent userAgent = userAgentDaoService.findByUserId(user.getId());
        if (userAgent.getLeftIncome() < 10)
            return buildReply(reply.build(), NOT_ENOUGH_GAMECOIN);
        long maxWithdrawalCoin = (long) (userAgent.getLeftIncome() - (userAgent.getLeftIncome() % 10));
        userAgentDaoService.decUserAgentCoin(user.getId(), maxWithdrawalCoin);
        double leftIncome = userAgent.decLeftIncome(maxWithdrawalCoin);
        EventPublisher.publish(new ModifyCoinEvent(user.getId(), maxWithdrawalCoin));
        withdrawalRecordDaoService.save(WithdrawalRecord.newInstance(user.getId(), maxWithdrawalCoin));
        return buildReply(reply.setLeftCoin(leftIncome).build(), SUCCESS);
    }

}
