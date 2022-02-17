package com.donglaistd.jinli.processors.handler.statistic;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.DailyBetInfoDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.statistic.DailyBetInfo;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.TimeUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.ResultCode.NOT_AUTHORIZED;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;


@Component
public class QueryFlowRecordRequestHandler extends MessageHandler {
    @Autowired
    private DailyBetInfoDaoService dailyBetService;

    @Autowired
    DataManager dataManager;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QueryFlowRecordReply.Builder reply = Jinli.QueryFlowRecordReply.newBuilder();
        if (Objects.isNull(user)) return buildReply(reply, NOT_AUTHORIZED);
        Jinli.QueryFlowRecordRequest request = messageRequest.getQueryFlowRecordRequest();
        Constant.QueryTimeType queryTimeType = request.getType();
        List<DailyBetInfo> infos = process(user, queryTimeType);
        List<Jinli.BetSummary> betSummaryList = toProto(infos);
        return buildReply(reply.addAllBetSummary(betSummaryList), SUCCESS);
    }


    private List<Jinli.BetSummary> toProto(List<DailyBetInfo> infos) {
        return infos.stream().map(s -> {
            try {
                return Jinli.BetSummary.newBuilder()
                        .setRoomId(s.getRoomId())
                        .setBet((int) s.getBetAmount())
                        .setGameType(s.getGameType())
                        .setTime(s.getTime())
                        .setWin(s.getWin())
                        .build();
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).sorted(Comparator.comparing(Jinli.BetSummary::getTime).reversed()).collect(Collectors.toList());
    }

    private List<DailyBetInfo> process(User user, Constant.QueryTimeType queryTimeType) {
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
        return dailyBetService.findByBetUserIdIsAndTimeBetween(user.getId(), startTime, endTime);
    }
}
