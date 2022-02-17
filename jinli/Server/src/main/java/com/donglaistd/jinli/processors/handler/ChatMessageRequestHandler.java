package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.ConditionType;
import com.donglaistd.jinli.database.dao.BlacklistDaoService;
import com.donglaistd.jinli.database.dao.BulletChatMessageRecordDaoService;
import com.donglaistd.jinli.database.dao.GlobalBlackListDaoService;
import com.donglaistd.jinli.database.dao.UserAttributeDaoService;
import com.donglaistd.jinli.database.entity.Blacklist;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.chat.BulletChatMessageRecord;
import com.donglaistd.jinli.database.entity.chat.MessageRecord;
import com.donglaistd.jinli.event.TaskEvent;
import com.donglaistd.jinli.service.BackOfficeSocketMessageService;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.service.RoomManagementHandlerService;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import com.donglaistd.jinli.util.WordFilterUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.DataManager.getRoomFromChannel;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;


@Component
public class ChatMessageRequestHandler extends MessageHandler {
    @Value("${max.bullet.chat.length}")
    private int MAX_BULLET_CHAT_LENGTH;

    @Value("${bullet.chat.time.limit}")
    private long BULLET_CHAT_TIME_LIMIT;

    @Autowired
    DataManager dataManager;
    @Autowired
    WordFilterUtil wordFilterUtil;
    @Autowired
    BlacklistDaoService blacklistDaoService;
    @Autowired
    BackOfficeSocketMessageService backOfficeSocketMessageService;
    @Autowired
    GlobalBlackListDaoService globalBlackListDaoService;
    @Autowired
    BulletChatMessageRecordDaoService bulletChatMessageRecordDaoService;
    @Autowired
    UserAttributeDaoService userAttributeDaoService;
    @Autowired
    RoomManagementHandlerService roomManagementHandlerService;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var request = messageRequest.getChatMessageRequest();
        var chatReply = Jinli.ChatMessageReply.newBuilder();
        var room = getRoomFromChannel(ctx);
        resultCode = verifyChatMessageRequest(room, request, user);
        if(Objects.equals(resultCode,SUCCESS)){
            String content = request.getContent();
            content = wordFilterUtil.replaceSensitiveWordAndNumber(content, "*");
            user.setLastBulletChatTime(Calendar.getInstance().getTime());
            var cbm = buildChatBroadcastMessage(user, content);
            MessageRecord messageRecord = MessageRecord.newInstance(content, user.getId());
            room.addChatMessageRecord(messageRecord,user.getPlatformType());
            dataManager.saveUser(user);
            saveBulletChatMessage(user,room,content);
            EventPublisher.publish(TaskEvent.newInstance(user.getId(), ConditionType.publicChat,1));
            backOfficeSocketMessageService.broadCastChatMessageToHttpSocket(room.getId(),user,messageRecord);
            room.broadCastByUser(buildReply(cbm),user);
        }
        return buildReply(chatReply, resultCode);
    }

    private Jinli.ChatBroadcastMessage buildChatBroadcastMessage(User user, String content){
        var cbm = Jinli.ChatBroadcastMessage.newBuilder();
        cbm.setDisplayName(user.getDisplayName());
        cbm.setContent(content);
        cbm.setVipId(user.getVipType());
        cbm.setLevel(user.getLevel()).setUserId(user.getId());
        return cbm.build();
    }

    private Constant.ResultCode verifyChatMessageRequest(Room room, Jinli.ChatMessageRequest request,User chatUser){

        if (room == null || !room.validateIsLiveUser(chatUser.getLiveUserId()) && !room.getAllAudience().contains(chatUser.getId())) {
            return NO_ENTERED_ROOM;
        }
        if (StringUtils.isNullOrBlank(request.getContent()) || request.getContent().length() > MAX_BULLET_CHAT_LENGTH) {
            return CHAT_MESSAGE_FORMAT_ERROR;
        }
        LiveUser liveUser = dataManager.findLiveUser(room.getLiveUserId());
        if(liveUser != null && liveUser.containsDisablePermission(Constant.LiveUserPermission.PERMISSION_CHAT)){
            return PERMISSION_DISABLED;
        }
        if (chatUser.getLastBulletChatTime() != null && Calendar.getInstance().getTimeInMillis() - chatUser.getLastBulletChatTime().getTime() < BULLET_CHAT_TIME_LIMIT) {
            return SEND_CHAT_MESSAGE_TOO_SOON;
        }
        if((roomManagementHandlerService.isMuteInRoom(room,blacklistDaoService.findByRoomId(room.getId()),chatUser))) {
            return YOU_HAVE_BEEN_MUTED;
        }
        return SUCCESS;
    }

    public void saveBulletChatMessage(User user, Room room, String content){
        if(!Objects.equals(user.getLiveUserId(),room.getLiveUserId())) {
            bulletChatMessageRecordDaoService.save(new BulletChatMessageRecord(content, user.getId(), room.getId(), user.getPlatformType()));
            DataManager.getUserRoomRecord(user.getId()).addBulletMessageCount();
        }
    }
}
