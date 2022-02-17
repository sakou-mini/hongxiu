package com.donglai.test.handle.message;

import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.test.entity.UserCache;
import com.donglai.test.handle.MessageHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LiveOfQueryLiveUserInfosReplyHandler extends MessageHandler {

    @Override
    protected void doHandle(ChannelHandlerContext ctx, HongXiu.HongXiuMessageReply messageReply, UserCache userCache) {
        var reply = messageReply.getLiveOfQueryLiveUserInfosReply();
        var liveUserInfos = reply.getLiveUserInfosList();
        Live.LiveUserInfo liveUserInfo = liveUserInfos.get(0);
        userCache.setLiveUserId(liveUserInfo.getLiveUserId());
        userCache.setRoomId(liveUserInfo.getRoomId());
    }
}
