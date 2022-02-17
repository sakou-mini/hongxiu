package com.donglai.live.service.impl.platform;

import com.donglai.live.constant.DuoCaiConstant;
import com.donglai.live.service.PlatformService;
import com.donglai.protocol.Constant;
import org.springframework.stereotype.Component;

@Component
public class DuoCaiPlatformServiceImpl implements PlatformService {

    @Override
    public String getPlatformDefaultAvatar(Constant.PlatformType platform) {
        return DuoCaiConstant.DEFAULT_AVATAR;
    }


}
