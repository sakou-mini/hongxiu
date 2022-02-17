package com.donglai.live.service.impl.platform;

import com.donglai.common.constant.PathConstant;
import com.donglai.live.service.PlatformService;
import com.donglai.protocol.Constant;
import org.springframework.stereotype.Component;

@Component
public class SportPlatformServiceImpl implements PlatformService {

    @Override
    public String getPlatformDefaultAvatar(Constant.PlatformType platform) {
        return PathConstant.DEFAULT_AVATAR_PATH;
    }


}
