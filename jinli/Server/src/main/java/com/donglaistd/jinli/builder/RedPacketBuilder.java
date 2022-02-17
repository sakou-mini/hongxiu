package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.config.RedPacketProperties;
import com.donglaistd.jinli.database.entity.RedPacket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedPacketBuilder {

    @Autowired
    RedPacketProperties redPacketProperties;

    public RedPacket create(String userId, int coinAmount, int redPacketNum) {
        var redPacket = RedPacket.getInstance(userId, coinAmount, redPacketNum, redPacketProperties.getMinRange());
        redPacket.setCountDownTime(redPacketProperties.getCountDownTime());
        redPacket.setExpireTime(redPacketProperties.getOverTime());
        return redPacket;
    }
}
