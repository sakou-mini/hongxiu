package com.donglaistd.jinli.processors.handler.live;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.statistic.LiveSourceLineConfigDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.system.LiveSourceLineConfig;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import com.donglaistd.jinli.util.VerifyUtil;
import com.donglaistd.jinli.util.live.LiveStream;
import com.donglaistd.jinli.util.live.LiveStreamFactory;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.ResultCode.LIVE_SOURCE_LINE_NOT_AVAILABLE;
import static com.donglaistd.jinli.Constant.ResultCode.PARAM_ERROR;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class SwitchLiveSourceLineRequestHandler extends MessageHandler {
    private final Logger logger = Logger.getLogger(SwitchLiveSourceLineRequestHandler.class.getName());

    @Autowired
    DataManager dataManager;
    @Autowired
    VerifyUtil verifyUtil;
    @Autowired
    LiveSourceLineConfigDaoService liveSourceLineConfigDaoService;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.SwitchLiveSourceLineRequest request = messageRequest.getSwitchLiveSourceLineRequest();
        Jinli.SwitchLiveSourceLineReply.Builder replyBuilder = Jinli.SwitchLiveSourceLineReply.newBuilder();
        if(!verifyUtil.checkIsLiveUser(user)) {
            return buildReply(replyBuilder, Constant.ResultCode.NOT_LIVE_USER);
        }
        LiveUser liveUser = dataManager.findLiveUser(user.getLiveUserId());
        Room room = DataManager.findOnlineRoom(liveUser.getRoomId());
        if (Objects.isNull(room)) {
            return buildReply(replyBuilder, Constant.ResultCode.ROOM_DOES_NOT_EXIST);
        }
        resultCode = processSwitchLiveSourceLine(room, liveUser, request);
        return buildReply(replyBuilder, resultCode);
    }

    public Constant.ResultCode processSwitchLiveSourceLine(Room room, LiveUser liveUser, Jinli.SwitchLiveSourceLineRequest request){
        Constant.LiveSourceLine liveSourceLine = request.getLiveSourceLine();
        String liveDomain = request.getLiveDomain();
        LiveSourceLineConfig liveSourceLineConfig = liveSourceLineConfigDaoService.findLiveSourceLineConfigByPlatformType(Constant.PlatformType.PLATFORM_JINLI);
        if(!liveSourceLineConfig.liveSourceLineIsAvailable(liveSourceLine))
            return LIVE_SOURCE_LINE_NOT_AVAILABLE;
        if(StringUtils.isNullOrBlank(liveDomain)) {
            logger.warning("liveDomain is empty");
            return PARAM_ERROR;
        }
        room.setLiveSourceLine(liveSourceLine);
        room.setLiveDomain(liveDomain);
        dataManager.saveRoom(room);
        Jinli.SwitchLiveSourceLineBroadcastMessage broadcastMessage = buildSwitchLiveSourceLineBroadcast(liveDomain, liveUser, liveSourceLine);
        room.broadCastToAllPlatform(buildReply(broadcastMessage));
        return Constant.ResultCode.SUCCESS;
    }

    public Jinli.SwitchLiveSourceLineBroadcastMessage buildSwitchLiveSourceLineBroadcast(String liveDomain, LiveUser liveUser, Constant.LiveSourceLine liveSourceLine){
        LiveStream liveStream = LiveStreamFactory.getLiveStreamByLiveSourceLine(liveSourceLine);
        String pushUrl =  Objects.isNull(liveStream) ? "" : liveStream.getRtmpPushUrl(liveDomain, liveUser.getLiveUrl());
        List<String> pullUrls = Objects.isNull(liveStream) ? new ArrayList<>() : liveStream.getAllShareRtmpPullUrl(liveDomain, liveUser.getLiveUrl());
        return Jinli.SwitchLiveSourceLineBroadcastMessage.newBuilder().setLiveSourceLine(liveSourceLine).setPushUrl(pushUrl).addAllPullUrl(pullUrls).build();
    }
}
