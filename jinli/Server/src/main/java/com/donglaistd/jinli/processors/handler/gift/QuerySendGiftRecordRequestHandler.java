package com.donglaistd.jinli.processors.handler.gift;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.GiftLogDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.rank.GiftLog;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QuerySendGiftRecordRequestHandler extends MessageHandler {
    final GiftLogDaoService giftLogDaoService;
    final UserDaoService userDaoService;

    public QuerySendGiftRecordRequestHandler(GiftLogDaoService giftLogDaoService, UserDaoService userDaoService) {
        this.giftLogDaoService = giftLogDaoService;
        this.userDaoService = userDaoService;
    }

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QuerySendGiftRecordRequest request = messageRequest.getQuerySendGiftRecordRequest();
        List<GiftLog> giftLogs = giftLogDaoService.findBySenderIdAndPlatformType(user.getId(), user.getPlatformType(), request.getRecordSize());
        List<Jinli.GiftRecord> giftRecords = new ArrayList<>();
        Map<String, User> receiverMap = new HashMap<>();
        User receiver;
        for (GiftLog giftLog : giftLogs) {
            receiver = receiverMap.computeIfAbsent(giftLog.getReceiveId(), k -> userDaoService.findById(giftLog.getReceiveId()));
            giftRecords.add(giftLog.toProto().setLiveUserId(receiver.getLiveUserId()).build());
        }
        Jinli.QuerySendGiftRecordReply.Builder replyBuilder = Jinli.QuerySendGiftRecordReply.newBuilder().addAllGiftRecords(giftRecords);
        return buildReply(replyBuilder, Constant.ResultCode.SUCCESS);
    }
}
