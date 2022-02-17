package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.MusicListDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.http.service.OfficialLiveService;
import com.donglaistd.jinli.service.LiveProcess;
import com.donglaistd.jinli.service.QueueProcess;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;


@Component
public class EndLiveRequestHandler extends MessageHandler {
    @Autowired
    DataManager dataManager;

    private static final Logger logger = Logger.getLogger(EndLiveRequestHandler.class.getName());
    @Autowired
    MusicListDaoService musicListDaoService;
    @Autowired
    OfficialLiveService officialLiveService;
    @Autowired
    QueueProcess queueProcess;
    @Autowired
    LiveProcess liveProcess;
    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        LiveUser liveUser = dataManager.findLiveUser(user.getLiveUserId());
        return process(liveUser,messageRequest.getEndLiveRequest().getEndLiveCountDownTime(), Constant.EndType.NORMAL_END);
    }

    public Jinli.JinliMessageReply process(LiveUser liveUser,long endLiveDelayTime, Constant.EndType endType) {
        Jinli.EndLiveReply.Builder reply = Jinli.EndLiveReply.newBuilder();
        Constant.ResultCode resultCode = liveProcess.verifyEndLive(liveUser);
        if(Objects.equals(resultCode,SUCCESS)){
            liveProcess.delayEndLiveGame(endLiveDelayTime,liveUser,endType);
            reply.setRtmpCode(liveUser.getRtmpCode());
            queueProcess.deleteLiveLimitQueue(liveUser.getId());
        }
        return buildReply(reply, this.resultCode);
    }
}
