package com.donglaistd.jinli.processors.handler.redpacket;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.*;
import com.donglaistd.jinli.database.entity.*;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;
/**
 * @deprecated
 */
@Component
public class GrabRedPacketRequestHandler extends MessageHandler {
    private static final java.util.logging.Logger logger = Logger.getLogger(GrabRedPacketRequestHandler.class.getName());
    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    RedPacketRecordDaoService redPacketRecordDaoService;
    @Autowired
    private FollowListDaoService followListDaoService;
    @Autowired
    private LiveUserDaoService liveUserDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    RoomDaoService roomDaoService;

    @Override
    @Transactional
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.GrabRedPacketRequest request = messageRequest.getGrabRedPacketRequest();
        Jinli.GrabRedPacketReply.Builder reply = Jinli.GrabRedPacketReply.newBuilder();
      /*   String redPacketId = request.getRedPacketId();
        Room room = DataManager.getRoomFromChannel(ctx);
        if (Objects.isNull(room))
            room = DataManager.closeRoomInfo.get(ctx.channel().attr(ROOM_KEY).get());
        if (Objects.isNull(room)) return buildReply(reply, ROOM_DOES_NOT_EXIST);
        RedPacket redPacket = room.getRedPacket(redPacketId);
        if (Objects.isNull(redPacket)) return buildReply(reply, REDPACKET_NOT_EXISTS);
        Constant.ResultCode resultCode = verifyRedPacket(redPacket, room, user);
        if (!Objects.equals(SUCCESS, resultCode)) return buildReply(reply, resultCode);
        Jinli.JinliMessageReply messageReply = dealOpenGetRedPacket(user, redPacket);
        updateRoomNextRedPacketQueue(room, redPacket);
        if (Objects.nonNull(DataManager.findOnlineRoom(room.getId())))
            dataManager.saveRoom(room);
        else
            roomDaoService.save(room);*/
        return buildReply(reply);
    }

    /*public void updateRoomNextRedPacketQueue(Room room, RedPacket redPacket) {
        if (redPacket.isOver()) {
            room.cleanRedPacketTimeTask();
            room.popRedPacketQueue(redPacket);
            RedPacket nextQueue = room.getFirstRedPacketQueue();
            if (Objects.nonNull(nextQueue) && nextQueue.getOpenTime() <= 0) {
                room.startNextRedPacket();
            }
        }
    }*/


    public Constant.ResultCode verifyRedPacket(RedPacket redPacket, Room room, User user) {
        long now = System.currentTimeMillis();
        long openTime = redPacket.getOpenTime();
        if (openTime <= 0 || now < redPacket.getOpenTime())
            return REDPACKET_NOT_READY;
        //CONFIG
        switch (redPacket.getConfig()) {
            case FOLLOW_LIVEUSER:
                var liveUser = liveUserDaoService.findById(room.getLiveUserId());
                boolean isFollower = followListDaoService.findAllByFollower(user).stream().map(FollowList::getFollowee).collect(Collectors.toList()).contains(liveUser);
                if (!isFollower) return NOT_FOLLOWING;
                break;
            case GIFT:
                break;
        }
        if (redPacket.containUser(user.getId()))
            return ALREADY_OPEN_REDPACKET;
        return SUCCESS;
    }

    public Jinli.JinliMessageReply dealOpenGetRedPacket(User user, RedPacket redPacket) {
        String userId = user.getId();
        Jinli.GrabRedPacketReply.Builder reply = Jinli.GrabRedPacketReply.newBuilder();
        if (Objects.isNull(redPacket) || redPacket.isOver()) return buildReply(reply, REDPACKET_OVER);
        if (redPacket.containUser(userId)) return buildReply(reply, ALREADY_OPEN_REDPACKET);
        Integer redPacketCoin = redPacket.getRedPacketCoin();
        if (redPacketCoin <= 0) return buildReply(reply, REDPACKET_OVER);
        redPacket.addRecord(userId);
        RedPacketRecord redPacketRecord = RedPacketRecord.getInstance(redPacket.getId(), userId, redPacketCoin);
        redPacketRecordDaoService.save(redPacketRecord);
        EventPublisher.publish(new ModifyCoinEvent(user, redPacketCoin));
        reply.setCoin(redPacketCoin).setTotalCoin(user.getGameCoin() + redPacketCoin).setLeftNum(redPacket.getLeftRedPacketNum().get());
        return buildReply(reply, SUCCESS);
    }
}
