package com.donglaistd.jinli.processors.handler.rank;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.FollowListDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;
/**
 * @deprecated
 */
@Component
public class GetLiveUserRankRequestHandler extends MessageHandler {
    @Autowired
    private FollowListDaoService followListDaoService;
    @Autowired
    DataManager dataManager;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.GetLiveUserRankReply.Builder builder = Jinli.GetLiveUserRankReply.newBuilder();
        var userIds = followListDaoService.findFolloweeIdsByFollowerId(user.getLiveUserId());
        List<Jinli.LiveUserRankInfo> rank = DataManager.liveRank;
        rank.forEach(l -> {
            l.toBuilder().setIsFollow(userIds.contains(l.getUserId())).build();
        });
        resultCode = Constant.ResultCode.SUCCESS;
        return buildReply(builder.addAllLiveUserRankInfo(rank), resultCode);
    }
}