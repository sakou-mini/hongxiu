package com.donglaistd.jinli.processors.handler.gift;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.GiftLogDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.rank.GiftLog;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.TimeUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QueryGiftIncomeDetailRequestHandler extends MessageHandler {

    @Autowired
    GiftLogDaoService giftLogDaoService;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QueryGiftIncomeDetailReply.Builder reply = Jinli.QueryGiftIncomeDetailReply.newBuilder();
        long startTime = TimeUtil.getCurrentDayStartTime();
        long endTime = TimeUtil.getDayEndTime(System.currentTimeMillis());
        List<GiftLog> giftLog = giftLogDaoService.findByReceiverIdAndGroupByGift(user.getId(), startTime, endTime);
        Map<String, List<GiftLog>> userGiftInfo = giftLog.stream().collect(Collectors.groupingBy(GiftLog::getSenderId));
        Map<String, Long> giftAmount = giftLog.stream().collect(Collectors.groupingBy(GiftLog::getSenderId, Collectors.summingLong(GiftLog::getSendAmount)));
        List<String> rankUserId = giftAmount.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).map(Map.Entry::getKey).collect(Collectors.toList());
        rankUserId.forEach(id -> reply.addGiftIncomeDetails(buildGiftIncomeDetail(id, userGiftInfo.get(id))));
        reply.setTotalIncome(giftAmount.values().stream().mapToLong(Long::longValue).sum());
        return buildReply(reply, Constant.ResultCode.SUCCESS);
    }

    public Jinli.GiftIncomeDetail.Builder buildGiftIncomeDetail (String userId,List<GiftLog> giftLogs){
        Jinli.GiftIncomeDetail.Builder builder = Jinli.GiftIncomeDetail.newBuilder();
        User user = dataManager.findUser(userId);
        if(Objects.isNull(user)) return builder;
        builder.setAvatarUrl(user.getAvatarUrl()).setDisplayName(user.getDisplayName()).setUseId(userId);
        giftLogs.forEach(giftLog -> builder.addGiftRecords(giftLog.toProto()));
        return builder;
    }
}
