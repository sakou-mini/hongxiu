package com.donglai.live.message.services.rank;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.live.process.RankDataProcess;
import com.donglai.model.db.entity.live.GiftRank;
import com.donglai.model.db.service.live.GiftRankService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.stereotype.Service;

import static com.donglai.live.util.MessageUtil.buildReply;

@Service("LiveOfQueryContributionRankRequest")
public class LiveOfQueryContributionRankRequest_Service implements TopicMessageServiceI<String> {
    private final GiftRankService giftRankService;
    private final RankDataProcess rankDataProcess;

    public LiveOfQueryContributionRankRequest_Service(GiftRankService giftRankService, RankDataProcess rankDataProcess) {
        this.giftRankService = giftRankService;
        this.rankDataProcess = rankDataProcess;
    }

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var replyBuilder = Live.LiveOfQueryContributionRankReply.newBuilder();
        GiftRank giftRank = giftRankService.findByRankTypeAndTimeType(Constant.RankType.CONTRIBUTION_RANK);
        replyBuilder.addAllGiftRanks(rankDataProcess.buildRankInfo(giftRank));
        return buildReply(userId, replyBuilder.build(), Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
