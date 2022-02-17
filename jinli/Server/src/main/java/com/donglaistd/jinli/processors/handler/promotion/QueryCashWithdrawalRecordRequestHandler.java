package com.donglaistd.jinli.processors.handler.promotion;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.invite.WithdrawalRecordDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.invite.WithdrawalRecord;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QueryCashWithdrawalRecordRequestHandler extends MessageHandler {
    @Value("${withdrawal.record.max.num}")
    private long withdrawalRecordNum;

    @Autowired
    WithdrawalRecordDaoService withdrawalRecordDaoService;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        List<WithdrawalRecord> records = withdrawalRecordDaoService.findUserLatestRecords(user.getId(), withdrawalRecordNum);
        Jinli.QueryCashWithdrawalRecordReply.Builder reply = Jinli.QueryCashWithdrawalRecordReply.newBuilder();
        records.forEach(record -> reply.addWithdrawalRecords(Jinli.WithdrawalRecord.newBuilder()
                .setCoin(record.getCoinFlow()).setTime(record.getTime())));
        return buildReply(reply, Constant.ResultCode.SUCCESS);
    }
}
