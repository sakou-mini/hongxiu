package com.donglaistd.jinli.processors.handler.rank;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.GiftRankDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.rank.GiftRank;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.MockDataUtil;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QueryGiftRankRequestHandler extends MessageHandler {
    @Value("${data.rank.size}")
    private int RANK_SIZE;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private GiftRankDaoService giftRankDaoService;

    @Autowired
    UserDaoService userDaoService;
    /*
    *Constant.PlatformType.PLATFORM_DEFAULT means query all Platform rank
    * */
    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QueryGiftRankReply.Builder reply = Jinli.QueryGiftRankReply.newBuilder();
        Jinli.QueryGiftRankRequest request = messageRequest.getQueryGiftRankRequest();
        Constant.QueryTimeType timeType = Constant.QueryTimeType.ALL;
        GiftRank giftRank = giftRankDaoService.findByRankTypeAndTimeType(Constant.RankType.GIFT_RANK, timeType);
        if (Objects.nonNull(giftRank)) {
            List<Pair<String, Integer>> infos = giftRank.getRankInfoByPlatformType(Constant.PlatformType.PLATFORM_DEFAULT);
            infos.forEach(pair -> {
                User rankUser = userDaoService.findById(pair.getLeft());
                Jinli.RankInfo.Builder builder = Jinli.RankInfo.newBuilder();
                builder.setUserId(rankUser.getId())
                        .setLevel(rankUser.getLevel())
                        .setAvatarUrl(rankUser.getAvatarUrl())
                        .setDisplayName(rankUser.getDisplayName())
                        .setVipType(rankUser.getVipType())
                        .setAmount(pair.getRight())
                        .setLiveStatus(dataManager.findLiveUser(rankUser.getLiveUserId()).getLiveStatus())
                        .build();
                reply.addGiftRanks(builder);
            });
        }
        reply.addAllGiftRanks(MockDataUtil.mockGiftRackData(10));
        List<Jinli.RankInfo> giftRanksList = reply.getGiftRanksList();
        List<Jinli.RankInfo> rankInfoList = giftRanksList.stream().sorted(Comparator.comparing(Jinli.RankInfo::getAmount).reversed()).collect(Collectors.toList())
                .subList(0,Math.min(RANK_SIZE,giftRanksList.size()));
        reply.clearGiftRanks().addAllGiftRanks(rankInfoList);
        return buildReply(reply, SUCCESS);
    }

}
