package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.live.LiveStream;
import com.donglaistd.jinli.util.live.LiveStreamFactory;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;


@Component
public class SwitchPatternRequestHandler extends MessageHandler {
    @Autowired
    DataManager dataManager;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.SwitchPatternRequest request = messageRequest.getSwitchPatternRequest();
        Constant.Pattern pattern = request.getPattern();
        Jinli.SwitchPatternReply.Builder reply = Jinli.SwitchPatternReply.newBuilder();
        var liveUser = dataManager.findLiveUser(user.getLiveUserId());
        if (liveUser == null || liveUser.getRoomId() == null) {
            return buildReply(reply, NOT_LIVE_USER);
        }
        Room room = DataManager.roomMap.get(liveUser.getRoomId());
        if (Objects.nonNull(room)) {
            if (room.getPattern() == null || room.getPattern().equals(pattern)) {
                return buildReply(reply, MISSING_OR_ILLEGAL_PARAMETERS);
            }
            room.setPattern(pattern);
            dataManager.saveRoom(room);
            Jinli.SwitchPatternBroadcastMessage.Builder builder = Jinli.SwitchPatternBroadcastMessage.newBuilder();
            LiveStream liveStream = LiveStreamFactory.getLiveStreamByLiveSourceLine(room.getLiveSourceLine());
            if(liveStream != null) {
                String liveDomain = room.getLiveDomain();
                String rtmpPushUrl = liveStream.getRtmpPushUrl(liveDomain, liveUser.getLiveUrl());
                List<String> rtmpPullUrls = liveStream.getAllShareRtmpPullUrl(liveDomain,liveUser.getLiveUrl());
                builder.setPushUrl(rtmpPushUrl).addAllPullUrl(rtmpPullUrls);
            }
            builder.setPattern(pattern);
            room.broadCastToAllPlatform(buildReply(builder, null));
        } else {
            return buildReply(reply, ROOM_DOES_NOT_EXIST);
        }
        return buildReply(reply.setPattern(pattern), SUCCESS);
    }
}