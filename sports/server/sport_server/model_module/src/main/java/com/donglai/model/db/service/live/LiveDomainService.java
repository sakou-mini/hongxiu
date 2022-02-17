package com.donglai.model.db.service.live;

import com.donglai.model.db.entity.live.LiveDomain;
import com.donglai.model.db.repository.live.LiveDomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.donglai.common.constant.LiveRedisConstant.LIVE_LINE_DOMAIN;

@Service
public class LiveDomainService {
    @Autowired
    LiveDomainRepository liveDomainRepository;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Cacheable(value = LIVE_LINE_DOMAIN, key="#lineCode", unless="#result == null")
    public LiveDomain findByLineCode(int lineCode) {
        return liveDomainRepository.findByLineCode(lineCode);
    }

    @CachePut(value = LIVE_LINE_DOMAIN, key="#result.lineCode", unless="#result == null")
    public LiveDomain save(LiveDomain liveDomain){
        return liveDomainRepository.save(liveDomain);
    }

    public void deleteAllDomainConfig() {
        Set<String> keys = redisTemplate.keys(LIVE_LINE_DOMAIN + "::*");
        if(keys!=null && !keys.isEmpty()) redisTemplate.delete(keys);
        liveDomainRepository.deleteAll();
    }

    public List<LiveDomain> findAll(){
        return liveDomainRepository.findAll();
    }
}
