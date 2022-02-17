package com.donglaistd.jinli.processors.handler;

import cn.hutool.json.JSONObject;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.annotation.IgnoreAuth;
import com.donglaistd.jinli.annotation.IgnoreShutDown;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.service.LoginProcess;
import com.donglaistd.jinli.service.redis.RedisService;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.SessionUtil;
import com.donglaistd.jinli.util.VerifyUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.logging.Logger;

import static com.donglaistd.jinli.constant.CacheNameConstant.getUserSessionKey;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@IgnoreAuth
@IgnoreShutDown
@Component
public class SessionConnRequestHandler extends MessageHandler{
    private static final Logger LOGGER = Logger.getLogger(SessionConnRequestHandler.class.getName());
    @Autowired
    RedisService redisService;
    @Autowired
    DataManager dataManager;
    @Autowired
    RoomDaoService roomDaoService;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    VerifyUtil verifyUtil;
    @Autowired
    LoginProcess loginProcess;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var request = messageRequest.getSessionConnRequest();
        var reply = Jinli.SessionConnReply.newBuilder();
        String clientSession = request.getClientSession();
        JSONObject requestSession = SessionUtil.deCodeToJSon(clientSession);
        //校验session是否合法
        LOGGER.info("收到 断线重连, session is:"+clientSession);
        if(!verifySession(clientSession)){
            resultCode = Constant.ResultCode.CONN_SESSION_ILLEGALITY;
        }
        else {
            String userId = requestSession.getStr(SessionUtil.KEY_USER_ID);
            Channel oldChannel = DataManager.getUserChannel(userId);
            if(Objects.isNull(oldChannel)){
                resultCode = Constant.ResultCode.CONN_TIMEOUT;
            }else{
                resultCode = Constant.ResultCode.SUCCESS;
                String domain = Objects.isNull(oldChannel) ? "" : DataManager.getDomainKeyFromChannel(oldChannel);
                user = dataManager.findUser(userId);
                //1.断开并覆盖旧连接
                checkAndKickOutOldConnected(ctx, userId);
                //2.更新channel 属性
                DataManager.saveUserKeyToChannel(ctx, user.getId());
                dataManager.putUserChannel(user.getId(), ctx.channel());
                DataManager.saveDomainKeyToChannel(ctx,domain);
                DataManager.saveRoomKeyToChannel(ctx,getUserLinkRoomId(user));
                //3.销毁session，创建新的session
                String session = loginProcess.generatedAndSaveUserSession(user);
                //清除定时清除玩家的channel缓存任务
                DataManager.removeUserDisconnectTask(userId);
                DataManager.removeEndLiveTask(userId);
                reply.setClientSession(session);
            }
        }
        LOGGER.info("断线重连结果 为 :"+resultCode);
        return buildReply(reply,resultCode);
    }

    private void checkAndKickOutOldConnected(ChannelHandlerContext ctx, String userId){
        Channel oldChannel = DataManager.getUserChannel(userId);
        if(Objects.nonNull(oldChannel) && !oldChannel.equals(ctx.channel())){
            oldChannel.disconnect();
        }
    }

    public boolean verifySession(String clientSession){
        JSONObject requestSession = SessionUtil.deCodeToJSon(clientSession);
        String userId = (String) requestSession.get(SessionUtil.KEY_USER_ID);
        String sessionKey = getUserSessionKey(userId);
        String userSession = (String) redisService.get(sessionKey);
        User user = dataManager.findUser(userId);
        LOGGER.warning("客户端的session："+ clientSession +"\n"+"服务器session："+userSession);
        return !StringUtil.isNullOrEmpty(userSession) && !Objects.isNull(user) && Objects.equals(userSession, clientSession);
    }

    public String getUserLinkRoomId(User user){
        String roomId = null;
        String liveUserId = dataManager.getUserEnterRoomRecord(user.getId());
        if (!StringUtil.isNullOrEmpty(liveUserId)) {
            LiveUser liveUser = liveUserDaoService.findById(liveUserId);
            if(Objects.nonNull(liveUser)){
                Room onlineRoom = DataManager.findOnlineRoom(liveUser.getRoomId());
                if(onlineRoom != null && onlineRoom.isLive())  roomId = onlineRoom.getId();
            }
        }else{
            if(verifyUtil.checkIsLiveUser(user))
                roomId = liveUserDaoService.findById(user.getLiveUserId()).getRoomId();
        }
        return roomId;
    }
}
