package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.FollowListDaoService;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.service.LiveProcess;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.Constant.ResultCode.USER_NOT_FOUND;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class LiveUserInfoRequestHandler extends MessageHandler {
    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    RoomDaoService roomDaoService;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    LiveProcess liveProcess;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var message = messageRequest.getLiveUserInfoRequest();
        user = userDaoService.findById(message.getUserId());
        var reply = Jinli.LiveUserInfoReply.newBuilder();
        if (user == null || user.getLiveUserId() == null) {
            return buildReply(reply, USER_NOT_FOUND);
        }
        var liveUser = dataManager.findLiveUser(user.getLiveUserId());

        if(liveUser==null){
            liveUser = liveUserDaoService.findById(user.getLiveUserId());
        }
        Room room = DataManager.findOnlineRoom(liveUser.getRoomId());
        if(room == null){
            room = roomDaoService.findByLiveUser(liveUser);
        }
        var liveUserInfo = liveProcess.buildLiveUserInfo(liveUser, room);
        reply.setLiveUserInfo(liveUserInfo);
        return buildReply(reply, SUCCESS);
    }
}
