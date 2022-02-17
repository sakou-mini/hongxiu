package com.donglaistd.jinli.listener;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.GiftLogDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.rank.GiftLog;
import com.donglaistd.jinli.event.BaseEvent;
import com.donglaistd.jinli.event.GiftEvent;
import com.donglaistd.jinli.service.BackOfficeSocketMessageService;
import com.donglaistd.jinli.service.FansContributionService;
import com.donglaistd.jinli.service.UserOperationService;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import static com.donglaistd.jinli.constant.GameConstant.AUDIENCE_SIZE;

@Component
public class GiftListener implements EventListener {
    private static final java.util.logging.Logger logger = Logger.getLogger(GiftListener.class.getName());

    @Autowired
    private DataManager dataManager;
    @Autowired
    private GiftLogDaoService giftLogDaoService;
    @Autowired
    BackOfficeSocketMessageService backOfficeSocketMessageService;
    @Autowired
    UserOperationService userOperationService;
    @Autowired
    FansContributionService fansContributionService;
    @Autowired
    VerifyUtil verifyUtil;

    @Override
    public boolean handle(BaseEvent event) {
        logger.info("Gift received event");
        var giftEvent = (GiftEvent) event;
        User sendUser = giftEvent.getSendUser();
        User receiveUser = giftEvent.getReceiveUser();
        if (Objects.isNull(giftEvent.getSendUser()) || Objects.isNull(receiveUser)) return false;
        int sendAmount = giftEvent.getSendAmount();
        userOperationService.updateUserExp(sendUser, sendAmount);
        Optional.ofNullable(DataManager.findOnlineRoom(sendUser.getCurrentRoomId())).ifPresent(r -> r.updateGiftRank(sendUser, sendAmount));
        LiveUser liveUser = dataManager.findLiveUser(receiveUser.getLiveUserId());
        GiftLog giftLog = GiftLog.newInstance(sendUser.getId(), receiveUser.getId(), sendAmount, giftEvent.getGiftId(), giftEvent.getSendNum(), sendUser.getPlatformType());
        if(!sendUser.isPlatformUser() || (sendUser.isPlatformUser() && !verifyUtil.checkIsLiveUser(sendUser))){
            giftLogDaoService.save(giftLog);
        }
        dataManager.saveUser(sendUser);
        broadCastSendGift(giftEvent,liveUser);
        backOfficeSocketMessageService.broadCastSendGiftMessageToHttpSocket(sendUser, giftLog);
        fansContributionService.updateContributionScore(sendUser.getId(),receiveUser.getId(),sendAmount);
        DataManager.getUserRoomRecord(sendUser.getId()).addGiftCoin(sendAmount);
        return true;
    }

    private void broadCastSendGift(GiftEvent giftEvent,LiveUser liveUser) {
        User sendUser = giftEvent.getSendUser();
        User receiveUser = giftEvent.getReceiveUser();
        Room room = DataManager.findOnlineRoom(liveUser.getRoomId());
        if (room != null) {
            var sendGiftBroadcast = Jinli.SendGiftBroadcast.newBuilder().setSendAmount(giftEvent.getSendNum()).setGiftId(giftEvent.getGiftId())
                    .setSendUserId(sendUser.getId()).setSendUserVipId(sendUser.getVipType()).setDisplayName(sendUser.getDisplayName()).setReceiveUserId(receiveUser.getId())
                    .addAllLiveRank(room.getAudienceLiveRankIdByLimit(AUDIENCE_SIZE))
                    .build();
            room.broadCastToAllPlatform(Jinli.JinliMessageReply.newBuilder().setSendGiftBroadcast(sendGiftBroadcast).build());
        }
    }

    @Override
    public boolean isDisposable() {
        return false;
    }
}
