package com.donglaistd.jinli.processors.handler.rank;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.GiftLogDaoService;
import com.donglaistd.jinli.database.dao.GiftRankDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.rank.GiftRank;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.MockDataUtil;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QueryContributionRankRequestHandler extends MessageHandler {
    private static  final Logger logger = Logger.getLogger(QueryContributionRankRequestHandler.class.getName());
    @Value("${data.rank.size}")
    private int RANK_SIZE;
    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    private GiftRankDaoService giftRankDaoService;
    @Autowired
    GiftLogDaoService giftLogDaoService;

    /*
     *Constant.PlatformType.PLATFORM_DEFAULT means query all Platform rank
     * */
    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QueryContributionRankReply.Builder reply = Jinli.QueryContributionRankReply.newBuilder();
        Jinli.QueryContributionRankRequest request = messageRequest.getQueryContributionRankRequest();
        Constant.QueryTimeType timeType = Constant.QueryTimeType.ALL;
        GiftRank giftRank = giftRankDaoService.findByRankTypeAndTimeType(Constant.RankType.CONTRIBUTION_RANK, timeType);
        logger.info("GiftRank result is:"+giftRank);
        if (Objects.nonNull(giftRank)) {
            List<Pair<String, Integer>> infos = giftRank.getRankInfoByPlatformType(Constant.PlatformType.PLATFORM_DEFAULT);
            infos.forEach(pair -> {
                User rankUser = userDaoService.findById(pair.getLeft());
                if(rankUser!=null){
                    Jinli.RankInfo.Builder builder = Jinli.RankInfo.newBuilder();
                    builder.setUserId(rankUser.getId())
                            .setLevel(rankUser.getLevel())
                            .setAvatarUrl(rankUser.getAvatarUrl())
                            .setDisplayName(rankUser.getDisplayName())
                            .setVipType(rankUser.getVipType())
                            .setAmount(pair.getRight())
                            .build();
                    reply.addContributionRanks(builder);
                }else {
                    logger.warning("not found rankUser by id:"+pair.getLeft());
                }
            });
        }
        reply.addAllContributionRanks(MockDataUtil.mockContributeRankData(10));
        List<Jinli.RankInfo> contributionRanksList = reply.getContributionRanksList();
        List<Jinli.RankInfo> rankInfoList = contributionRanksList.stream().sorted(Comparator.comparing(Jinli.RankInfo::getAmount).reversed()).collect(Collectors.toList())
                .subList(0,Math.min(RANK_SIZE,contributionRanksList.size()));
        reply.clearContributionRanks().addAllContributionRanks(rankInfoList);
        return buildReply(reply, SUCCESS);
    }
}
