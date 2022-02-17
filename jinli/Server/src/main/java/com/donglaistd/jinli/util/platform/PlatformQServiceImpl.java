package com.donglaistd.jinli.util.platform;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.QPlatformGameConfigBuilder;
import com.donglaistd.jinli.database.dao.GiftOrderDaoService;
import com.donglaistd.jinli.database.entity.GiftOrder;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.system.GiftConfig;
import com.donglaistd.jinli.event.GiftEvent;
import com.donglaistd.jinli.http.entity.GiftSendInfo;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.HttpUtil;
import com.donglaistd.jinli.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.PlatformType.PLATFORM_Q;
import static com.donglaistd.jinli.Constant.ResultCode.NOT_LIVE_USER;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.constant.GameConstant.PLATFORMUSER_SPLITTER;
import static com.donglaistd.jinli.util.platform.PlatformCommonService.checkUserHasEnoughCoinForSendGift;

@Component
public class PlatformQServiceImpl implements IPlatformService {
    private final Logger logger = Logger.getLogger(PlatformQServiceImpl.class.getName());

    @Autowired
    QPlatformGameConfigBuilder qPlatformGameConfigBuilder;
    @Autowired
    DataManager dataManager;
    @Autowired
    GiftOrderDaoService giftOrderDaoService;
    @Autowired
    PlatformCommonService platformCommonService;
    @Autowired
    HttpUtil httpUtil;

    public boolean verifyPlatformStartLive(Jinli.LivePlatformParam livePlatformParam) {
        return !StringUtils.isNullOrBlank(livePlatformParam.getPlatformGameCode())
                && !StringUtils.isNullOrBlank(livePlatformParam.getPlatformRoomCode())
                && Objects.nonNull(qPlatformGameConfigBuilder.getPlatformConfig(livePlatformParam.getPlatformGameCode(), livePlatformParam.getPlatformRoomCode()));
    }

    @Override
    public Constant.ResultCode sendGift(String senderId, String receiverId, GiftConfig giftConfig, int sendAmount) {
        var totalPrice = giftConfig.getPrice() * sendAmount;
        User sender = dataManager.findUser(senderId);
        User receiver = dataManager.findUser(receiverId);
        LiveUser receiveLiveUser = dataManager.findLiveUser(receiver.getLiveUserId());
        if(Objects.isNull(receiveLiveUser)) return NOT_LIVE_USER;
        if(Objects.equals(Constant.GiftPriceType.GIFT_PRICE_GOLDBEAN,giftConfig.getGiftPriceType())) {
            if(!checkUserHasEnoughCoinForSendGift(giftConfig, sendAmount, sender)) return Constant.ResultCode.NOT_ENOUGH_GAMECOIN;
        }else {
            logger.info("gift of liveUserId:"+ receiver.getLiveUserId());
            Room room = DataManager.findOnlineRoom(receiveLiveUser.getRoomId());
            if(Objects.isNull(room)) return Constant.ResultCode.ROOM_DOES_NOT_EXIST;
            String[] split = sender.getAccountName().split(PLATFORMUSER_SPLITTER);
            String platformQAccountName = split.length >= 2 ? split[1] : sender.getAccountName();
            GiftOrder giftOrder = new GiftOrder(giftConfig.getGiftId(), sender.getId(), receiver.getId(), sendAmount, totalPrice);
            GiftSendInfo sendGiftInfo =  new GiftSendInfo(giftConfig,sendAmount,receiver);
            if(!httpUtil.requestRewardForPlatformQ(platformQAccountName, room.getOtherPlatformGameCode(PLATFORM_Q), totalPrice, sendGiftInfo, giftOrder.getId())) {
                return Constant.ResultCode.REQUEST_DEDUCTION_FEE_FAILED;
            }else giftOrderDaoService.save(giftOrder);
        }
        platformCommonService.updateUserCoinBySendGift(sender,receiver,giftConfig.getPriceType(),totalPrice,false);
        sender = dataManager.findUser(senderId);
        receiver = dataManager.findUser(receiverId);
        EventPublisher.publish(new GiftEvent(sender, receiver, giftConfig.getGiftId(), (int) totalPrice,sendAmount));
        return SUCCESS;
    }
}
