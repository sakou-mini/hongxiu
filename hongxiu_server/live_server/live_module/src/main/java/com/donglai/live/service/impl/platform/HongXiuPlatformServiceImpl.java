package com.donglai.live.service.impl.platform;

import com.donglai.live.constant.HongXiuConstant;
import com.donglai.live.service.PlatformService;
import com.donglai.protocol.Constant;
import org.springframework.stereotype.Component;

@Component
public class HongXiuPlatformServiceImpl implements PlatformService {

    @Override
    public String getPlatformDefaultAvatar(Constant.PlatformType platform) {
        return HongXiuConstant.DEFAULT_AVATAR;
    }
}
