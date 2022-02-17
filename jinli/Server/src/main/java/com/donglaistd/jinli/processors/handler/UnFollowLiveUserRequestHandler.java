package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.FollowListDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.FollowList;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.processors.handler.FollowLiveUserRequestHandler.broadcastFollowLiveUser;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class UnFollowLiveUserRequestHandler extends MessageHandler {

    @Autowired
    private UserDaoService userDaoService;

    @Autowired
    private FollowListDaoService followListDaoService;

    @Autowired
    DataManager dataManager;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var request = messageRequest.getUnFollowLiveUserRequest();
        var reply = Jinli.UnFollowLiveUserReply.newBuilder();
        var self = dataManager.getUserFromChannel(ctx);
        if (self == null) {
            return buildReply(reply, NOT_AUTHORIZED);
        }
        var userId = request.getUserId();
        if (Objects.equals(self.getId(), userId)) {
            buildReply(reply.setUserId(userId), UNKNOWN);
        }
        user = userDaoService.findById(userId);
        if (user == null) {
            return buildReply(reply.setUserId(userId), USER_NOT_FOUND);
        }
        var liveUser = dataManager.findLiveUser(user.getLiveUserId());
        if (liveUser == null) {
            return buildReply(reply.setUserId(userId), USER_NOT_FOUND);
        }
        List<FollowList> all = followListDaoService.findAllByFollower(self);
        boolean contains = all.stream().map(FollowList::getFollowee).map(LiveUser::getId).collect(Collectors.toList()).contains(liveUser.getId());
        if (!contains) {
            return buildReply(reply.setUserId(userId), NOT_FOLLOWING);
        }
        FollowList followList = all.stream().filter(s -> Objects.equals(s.getFollowee(), liveUser)).findFirst().orElseThrow(RuntimeException::new);
        followListDaoService.delete(followList);
        broadcastFollowLiveUser(DataManager.findOnlineRoom(liveUser.getRoomId()), user.getId(), liveUser.getUserId(), Constant.FollowType.UNFOLLOW);
        return buildReply(reply.setUserId(userId), SUCCESS);
    }
}
