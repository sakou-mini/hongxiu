package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.annotation.IgnoreShutDown;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.service.LiveLimitProcess;
import com.donglaistd.jinli.service.RoomProcess;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.VerifyUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.logging.Logger;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@IgnoreShutDown
@Component
public class StartLiveRequestHandler extends MessageHandler {
    @Autowired
    DataManager dataManager;
    @Autowired
    private RoomDaoService roomDaoService;
    @Autowired
    LiveLimitProcess liveLimitProcess;
    @Autowired
    RoomProcess roomProcess;
    @Autowired
    VerifyUtil verifyUtil;

    private static final Logger logger = Logger.getLogger(StartLiveRequestHandler.class.getName());

    @Override
    @Transactional
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var request = messageRequest.getStartLiveRequest();
        var reply = Jinli.StartLiveReply.newBuilder();
        LiveUser liveUser = dataManager.findLiveUser(user.getLiveUserId());
        Constant.ResultCode resultCode = verifyUtil.verifyStartLive(liveUser,request);
        if(!Objects.equals(resultCode,Constant.ResultCode.SUCCESS)) return buildReply(reply, resultCode);
        cleanGameStatue(liveUser);
        Room room = DataManager.roomMap.getOrDefault(liveUser.getRoomId(), roomDaoService.findByLiveUser(liveUser));
        if (Objects.isNull(room)) {
            logger.warning("the liveUser not found room ,data error by roomId:" + liveUser.getRoomId());
            resultCode = Constant.ResultCode.ROOM_DOES_NOT_EXIST;
        } else {
            resultCode = Constant.ResultCode.SUCCESS;
            roomProcess.initStartLiveRoom(room,liveUser,request);
            user.setCurrentRoomId(room.getId());
            dataManager.saveUser(user);
            liveLimitProcess.updateLiveUserAutoEndLiveTask(liveUser.getId());
            reply.setRtmpCode(liveUser.getRtmpCode());
        }
        logger.info("liveUser--->" + liveUser.getId() +"startLive SUCCESS by room---->" + room);
        return buildReply(reply, resultCode);
    }

    private void cleanGameStatue(LiveUser liveUser) {
        String playingGameId = liveUser.getPlayingGameId();
        if (Objects.nonNull(playingGameId)) {
            BaseGame baseGame = (BaseGame) DataManager.findGame(playingGameId);
            if (Objects.isNull(baseGame))
                liveUser.cleanLive();
        }
    }
}
