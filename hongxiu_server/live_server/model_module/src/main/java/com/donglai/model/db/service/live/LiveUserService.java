package com.donglai.model.db.service.live;

import com.donglai.model.db.entity.live.LiveUser;
import com.donglai.model.db.repository.live.LiveUserRepository;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static com.donglai.common.constant.LiveRedisConstant.LIVE_USER;


@Service
public class LiveUserService {
    @Autowired
    LiveUserRepository liveUserRepository;

    @Cacheable(value = LIVE_USER, key = "#id", unless = "#result == null")
    public LiveUser findById(String id) {
        if (StringUtil.isNullOrEmpty(id)) {
            return null;
        }
        return liveUserRepository.findById(id).orElse(null);
    }

    @CachePut(value = LIVE_USER, key = "#liveUser.id", unless = "#result == null")
    public LiveUser save(LiveUser liveUser) {
        Assert.notNull(liveUser, "liveUser must not be null");
        return this.liveUserRepository.save(liveUser);
    }

    public LiveUser findByUserId(String userId) {
        return liveUserRepository.findByUserId(userId);
    }
}
