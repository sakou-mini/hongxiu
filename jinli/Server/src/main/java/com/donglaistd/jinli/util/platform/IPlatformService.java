package com.donglaistd.jinli.util.platform;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.system.GiftConfig;

public interface IPlatformService {
    boolean verifyPlatformStartLive(Jinli.LivePlatformParam livePlatformParam);

    Constant.ResultCode sendGift(String senderId, String receiverId, GiftConfig giftConfig, int sendAmount);
}
