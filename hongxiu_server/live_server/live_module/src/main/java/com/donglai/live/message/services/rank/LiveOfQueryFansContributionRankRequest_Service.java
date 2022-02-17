package com.donglai.live.message.services.rank;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.live.process.RankDataProcess;
import com.donglai.model.db.entity.live.GiftLog;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.donglai.live.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("LiveOfQueryFansContributionRankRequest")
public class LiveOfQueryFansContributionRankRequest_Service implements TopicMessageServiceI<String> {
    private final RankDataProcess rankDataProcess;

    public LiveOfQueryFansContributionRankRequest_Service(RankDataProcess rankDataProcess) {
        this.rankDataProcess = rankDataProcess;
    }

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getLiveOfQueryFansContributionRankRequest();
        var replyBuilder = Live.LiveOfQueryFansContributionRankReply.newBuilder();
        String requestUserId = request.getUserId();
        Constant.QueryTimeType queryTimeType = request.getTimeType();
        List<GiftLog> giftLogs = rankDataProcess.getUserGiftLogRankByQueryTime(requestUserId, queryTimeType);
        replyBuilder.addAllGiftRanks(rankDataProcess.buildGiftLogToGiftRankInfo(giftLogs));
        return buildReply(userId, replyBuilder.build(), SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
