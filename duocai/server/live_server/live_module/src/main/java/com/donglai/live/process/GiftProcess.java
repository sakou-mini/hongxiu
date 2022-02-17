package com.donglai.live.process;

import com.donglai.live.entityBuilder.GiftBuilder;
import com.donglai.live.service.GiftRankCacheService;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.GiftInfo;
import com.donglai.model.db.entity.live.GiftLog;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.CoinFlowService;
import com.donglai.model.db.service.live.GiftLogService;
import com.donglai.model.db.service.live.LiveUserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.Live;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Objects;

import static com.donglai.protocol.Constant.ResultCode.*;

@Component
public class GiftProcess {
    @Value("${gift.pay.rate}")
    private BigDecimal giftPayRate;
    @Value("${live.rank.max.show.num}")
    private int liveShowNum;

    @Autowired
    GiftBuilder giftBuilder;
    @Autowired
    UserService userService;
    @Autowired
    UserProcess userProcess;
    @Autowired
    RoomService roomService;
    @Autowired
    LiveUserService liveUserService;
    @Autowired
    GiftLogService giftLogService;
    @Autowired
    CoinFlowService coinFlowService;
    @Autowired
    LiveProcess liveProcess;
    @Autowired
    GiftRankCacheService giftRankCacheService;

    //===========================SendGift=================================
    public Constant.ResultCode verifySendGiftParam(String receiverUser, String giftId, int giftNum) {
        if (Objects.isNull(userService.findById(receiverUser))) {
            return Constant.ResultCode.USER_NOT_FOUND;
        } else if (Objects.isNull(giftBuilder.getById(giftId))) {
            return GIFT_NOT_FOUND;
        } else if (giftNum <= 0) {
            return GIFT_NUM_ILLEGAL;
        }
        return SUCCESS;
    }

    @Transactional
    public Constant.ResultCode sendGift(String senderId, String receiverId, String giftId, int giftNum) {
        User sender = userService.findById(senderId);
        User receiver = userService.findById(receiverId);
        GiftInfo giftInfo = giftBuilder.getById(giftId);
        long giftTotalPrice = giftInfo.getPrice() * giftNum;
        if (sender.getCoinNumber() < giftTotalPrice) {
            return COIN_NOT_ENOUGH;
        } else {
            sender.descCoin(giftTotalPrice);
            userProcess.addUserExp(giftTotalPrice, sender);
            userService.save(sender);
            var giftPayAmount = giftPayRate.multiply(new BigDecimal(giftTotalPrice)).intValue();
            UserProcess.sendModifyUserCoinListenerRequest(receiverId, giftPayAmount);
            //add coin flow and giftRecord
            addGiftRecordAndCoinFlow(senderId, receiverId, giftInfo, giftNum);
            Room room = liveProcess.getOnlineRoomByLiveUserId(receiver.getLiveUserId());
            if (Objects.nonNull(room)) {
                room.calcRank(senderId, (int) giftTotalPrice);
                roomService.save(room);
                broadCastSendGiftMessage(room, sender, receiverId, giftId, giftNum);
            }
            return SUCCESS;
        }
    }

    private void addGiftRecordAndCoinFlow(String senderId, String receiverId, GiftInfo giftInfo, int giftNum) {
        long giftTotalPrice = giftInfo.getPrice() * giftNum;
        long giftPayAmount = giftPayRate.multiply(new BigDecimal(giftTotalPrice)).longValue();
        GiftLog giftLog = GiftLog.newInstance(senderId, receiverId, giftTotalPrice, giftInfo.getId(), giftNum);
        giftLogService.save(giftLog);
        coinFlowService.addGiftFlow(senderId, giftTotalPrice, true);
        coinFlowService.addGiftFlow(receiverId, giftPayAmount, false);
        giftRankCacheService.updateGiftRank(senderId, receiverId, giftTotalPrice);
    }

    private void broadCastSendGiftMessage(Room room, User sender, String receiverId, String giftId, int giftNum) {
        Live.LiveOfSendGiftBroadcastMessage message = Live.LiveOfSendGiftBroadcastMessage.newBuilder()
                .setGiftId(giftId)
                .setGiftNum(giftNum).setReceiveUserId(receiverId)
                .setSendUserId(sender.getId()).setNickname(sender.getNickname()).setSendUserVipId(sender.getVipLevel())
                .addAllLiveRank(room.getAudienceLiveRankIdByLimit(liveShowNum)).build();
        RoomProcess.broadCastMessage(room, message);
    }
}
