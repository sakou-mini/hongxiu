package com.donglaistd.jinli.util.platform;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.system.GiftConfig;
import com.donglaistd.jinli.event.GiftEvent;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.platform.PlatformCommonService.checkUserHasEnoughCoinForSendGift;

@Component
public class PlatformTServiceImpl implements IPlatformService {
    @Autowired
    DataManager dataManager;
    @Autowired
    PlatformCommonService platformCommonService;

    @Override
    public boolean verifyPlatformStartLive(Jinli.LivePlatformParam livePlatformParam) {
        return true;
    }

    @Override
    public Constant.ResultCode sendGift(String senderId, String receiverId, GiftConfig giftConfig, int sendAmount) {
        var totalPrice = giftConfig.getPrice() * sendAmount;
        User sender = dataManager.findUser(senderId);
        User receiver = dataManager.findUser(receiverId);
        if (!checkUserHasEnoughCoinForSendGift(giftConfig,sendAmount,sender)) return NOT_ENOUGH_GAMECOIN;
        LiveUser receiveLiveUser = dataManager.findLiveUser(receiver.getLiveUserId());
        if(Objects.isNull(receiveLiveUser)) return NOT_LIVE_USER;
        if(receiveLiveUser.containsDisablePermission(Constant.LiveUserPermission.PERMISSION_GIFT)) return PERMISSION_DISABLED;
        platformCommonService.updateUserCoinBySendGift(sender,receiver,giftConfig.getPriceType(),totalPrice,true);
        sender = dataManager.findUser(senderId);
        receiver = dataManager.findUser(receiverId);
        EventPublisher.publish(new GiftEvent(sender, receiver, giftConfig.getGiftId(), (int) totalPrice,sendAmount));
        return SUCCESS;
    }
}
