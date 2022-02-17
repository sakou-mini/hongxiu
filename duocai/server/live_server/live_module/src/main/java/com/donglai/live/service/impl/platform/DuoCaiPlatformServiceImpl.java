package com.donglai.live.service.impl.platform;

import com.donglai.live.service.PlatformService;
import com.donglai.protocol.Constant;
import org.springframework.stereotype.Component;

import static com.donglai.common.constant.PathConstant.DEFAULT_AVATAR_PATH;

@Component
public class DuoCaiPlatformServiceImpl implements PlatformService {

    @Override
    public String getPlatformDefaultAvatar(Constant.PlatformType platform) {
        return DEFAULT_AVATAR_PATH;
    }


}
