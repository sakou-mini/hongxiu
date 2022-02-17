package com.donglai.live.util;

import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.live.entityBuilder.GiftBuilder;
import com.donglai.model.db.entity.live.GiftInfo;

public class GiftUtil {
    private static final GiftBuilder giftBuilder = SpringApplicationContext.getBean(GiftBuilder.class);

    public static GiftInfo getById(String giftId) {
        return giftBuilder.getById(giftId);
    }
}
