package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.platform.PlatformRechargeRecordDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.plant.PlatformRechargeRecord;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QueryCoinRechargeRecordRequestHandler  extends MessageHandler{
    @Autowired
    PlatformRechargeRecordDaoService platformRechargeRecordDaoService;
    @Value("${rechargeRecord.max.show.num}")
    private int showNum;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var replyBuilder = Jinli.QueryCoinRechargeRecordReply.newBuilder();
        List<PlatformRechargeRecord> records = platformRechargeRecordDaoService.queryUserRechargeRecord(user.getId(), showNum);
        long totalRechargeCoin = platformRechargeRecordDaoService.totalRechargeCoinByUserId(user.getId());
        replyBuilder.setTotalRecharge(totalRechargeCoin);
        if(records!=null){
            for (PlatformRechargeRecord record : records) {
                replyBuilder.addRechargeInfo(buildRechargeInfo(record));
            }
        }
        return buildReply(replyBuilder, Constant.ResultCode.SUCCESS);
    }

    public Jinli.RechargeInfo  buildRechargeInfo(PlatformRechargeRecord rechargeRecord){
        return Jinli.RechargeInfo.newBuilder().setRechargeNum((int) rechargeRecord.getRechargeGameCoin())
                .setTime(String.valueOf(rechargeRecord.getRechargeTime())).build();
    }
}
