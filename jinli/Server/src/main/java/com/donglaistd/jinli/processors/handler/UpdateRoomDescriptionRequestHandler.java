package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.CONTENT_IS_EMPTY_OR_TOO_LONG;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;


@Component
public class UpdateRoomDescriptionRequestHandler extends MessageHandler {
    @Autowired
    private UserDaoService userDaoService;
    @Value("${data.live-room.description.length}")
    private int MAX_ROOM_DESCRIPTION_LENGTH;
    @Autowired
    RoomDaoService roomDaoService;

    @Autowired
    DataManager dataManager;

    @Override
    @Transactional
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.UpdateRoomDescriptionRequest request = messageRequest.getUpdateRoomDescriptionRequest();
        Jinli.UpdateRoomDescriptionReply.Builder reply = Jinli.UpdateRoomDescriptionReply.newBuilder();
        LiveUser onlineLiveUser = dataManager.findLiveUser(user.getLiveUserId());
        if (Objects.isNull(onlineLiveUser)) {
            return buildReply(reply, Constant.ResultCode.NOT_LIVE_USER);
        }
        String content = request.getContent();
        if (StringUtils.isNullOrBlank(content) || content.length() > MAX_ROOM_DESCRIPTION_LENGTH) {
            buildReply(reply, CONTENT_IS_EMPTY_OR_TOO_LONG);
        }
        Room room = DataManager.findOnlineRoom(onlineLiveUser.getRoomId());
        if(room == null)
            room = roomDaoService.findByLiveUser(onlineLiveUser);
        room.setRoomTitle(content);
        dataManager.saveRoom(room);
        userDaoService.save(user);
        return buildReply(reply, SUCCESS);
    }
}
