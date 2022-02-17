package com.donglai.web.builder;

import com.donglai.model.db.entity.live.LiveUser;
import com.donglai.model.db.service.live.LiveUserService;
import com.donglai.model.util.IdGeneratedProcess;
import com.donglai.protocol.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class LiveUserBuilder {
    @Value("${liveUserId.length}")
    private int idLength;
    @Autowired
    LiveUserService liveUserService;

    @Autowired
    IdGeneratedProcess idGeneratedProcess;

    public LiveUser createSimpleLiveUser(String userId, Constant.LiveUserStatus status, Constant.PlatformType platform) {
        LiveUser liveUser = liveUserService.findByUserId(userId);
        if (Objects.isNull(liveUser)) {
            liveUser = new LiveUser();
            liveUser.setId(idGeneratedProcess.generatedId(LiveUser.class.getSimpleName(), idLength));
            liveUser.setUserId(userId);
            liveUser.setApplyTime(System.currentTimeMillis());
            liveUser.setPlatform(platform);
        }
        liveUser.setStatus(status);
        liveUserService.save(liveUser);
        return liveUser;
    }
}
