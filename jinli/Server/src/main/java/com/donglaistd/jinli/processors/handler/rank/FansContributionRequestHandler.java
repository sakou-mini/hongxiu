package com.donglaistd.jinli.processors.handler.rank;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.GiftLogDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.rank.GiftLog;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.service.FansContributionService;
import com.donglaistd.jinli.util.TimeUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.ResultCode.NOT_AUTHORIZED;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class FansContributionRequestHandler extends MessageHandler {
    @Value("${data.rank.size}")
    private int SIZE;
    @Autowired
    private GiftLogDaoService giftLogDaoService;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    FansContributionService fansContributionService;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        String userId = messageRequest.getFansContributionRequest().getUserId();
        Jinli.FansContributionReply.Builder reply = Jinli.FansContributionReply.newBuilder();
        if (Objects.isNull(user)) return buildReply(reply, NOT_AUTHORIZED);
        Constant.QueryTimeType timeType = Constant.QueryTimeType.ALL;
        List<GiftLog> giftLogs = fansContributionService.queryFansContributionRank(userId, SIZE);
        //List<GiftLog> giftLogs = process(userId, SIZE, timeType);
        List<Jinli.RankInfo> list = toProto(giftLogs);
        /*list.addAll(MockDataUtil.mockFansRankData(10));*/
        list.sort(Comparator.comparing(Jinli.RankInfo::getAmount).reversed());
        return buildReply(reply.addAllFansContribution(list), SUCCESS);
    }

    private List<Jinli.RankInfo> toProto(List<GiftLog> giftLogs) {
        return giftLogs.stream().map(g -> {
            Jinli.RankInfo.Builder builder = Jinli.RankInfo.newBuilder();
            User user = userDaoService.findById(g.getSenderId());
            if (Objects.nonNull(user)) {
                builder.setUserId(user.getId())
                        .setLevel(user.getLevel())
                        .setAvatarUrl(user.getAvatarUrl())
                        .setDisplayName(user.getDisplayName())
                        .setVipType(user.getVipType())
                        .setAmount(g.getSendAmount());
            }
            return builder.build();
        }).collect(Collectors.toList());
    }

    private List<GiftLog> process(String userId, int size, Constant.QueryTimeType queryTimeType) {
        long endTime = System.currentTimeMillis();
        long startTime = -1;
        switch (queryTimeType) {
            case TODAY:
                startTime = TimeUtil.getCurrentDayStartTime();
                break;
            case WEEK:
                startTime = TimeUtil.getFirstDayOfCurrentWeeks();
                break;
            case MONTH:
                startTime = TimeUtil.getFirstDayOfCurrentMonth();
                break;
            default:
                break;
        }
        return giftLogDaoService.findByLiveUserIdAndGroupSenderId(userId, size, startTime, endTime);
    }
}
