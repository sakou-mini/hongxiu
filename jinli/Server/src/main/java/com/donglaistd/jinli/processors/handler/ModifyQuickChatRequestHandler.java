package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.service.LiveProcess;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.VerifyUtil;
import com.donglaistd.jinli.util.WordFilterUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class ModifyQuickChatRequestHandler extends MessageHandler {
    final DataManager dataManager;
    final VerifyUtil verifyUtil;
    final LiveProcess liveProcess;
    @Value("${quick.chat.max.num}")
    private int quickChatMaxNum;
    @Value("${quick.chat.max.length}")
    private int quickChatMaxLength;

    public ModifyQuickChatRequestHandler(DataManager dataManager, VerifyUtil verifyUtil, LiveProcess liveProcess) {
        this.dataManager = dataManager;
        this.verifyUtil = verifyUtil;
        this.liveProcess = liveProcess;
    }

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var request = messageRequest.getModifyQuickChatRequest();
        var replyBuilder = Jinli.ModifyQuickChatReply.newBuilder();
        var liveUser = dataManager.findLiveUser(user.getLiveUserId());
        List<String> quickChats = new Gson().fromJson(request.getQuickChat(), new TypeToken<List<String>>() {}.getType());
        if(quickChats.size() > quickChatMaxNum || quickChats.stream().anyMatch(words -> WordFilterUtil.getWordLength(words) > quickChatMaxLength)){
            resultCode = Constant.ResultCode.QUICK_CHAT_ILLEGALITY;
        } else if(!verifyUtil.verifyIsLiveUser(liveUser)) {
            resultCode = Constant.ResultCode.NOT_LIVE_USER;
        } else if(liveUser.containsDisablePermission(Constant.LiveUserPermission.PERMISSION_EDIT_QUICK_CHAT)){
            resultCode = Constant.ResultCode.PERMISSION_DISABLED;
            Room room = DataManager.findOnlineRoom(liveUser.getRoomId());
            if(Objects.nonNull(room) && room.isLive()) broadCastModifyQuickCharMessage(liveUser, room);
        } else {
            resultCode = Constant.ResultCode.SUCCESS;
            String quickChat = request.getQuickChat();
            liveUser.setQuickChat(quickChat);
            dataManager.saveLiveUser(liveUser);
            Room room = DataManager.findOnlineRoom(liveUser.getRoomId());
            if(Objects.nonNull(room) && room.isLive()) broadCastModifyQuickCharMessage(liveUser, room);
        }
        return buildReply(replyBuilder,resultCode);
    }

    public void broadCastModifyQuickCharMessage(LiveUser liveUser,Room room) {
        Jinli.LiveUserInfo liveUserInfo = liveProcess.buildLiveUserInfo(liveUser, room);
        var broadcastMessage = Jinli.ModifyQuickChatBroadcastMessage.newBuilder().setLiveUserInfo(liveUserInfo);
        room.broadCastToAllPlatform(buildReply(broadcastMessage));
    }
}
