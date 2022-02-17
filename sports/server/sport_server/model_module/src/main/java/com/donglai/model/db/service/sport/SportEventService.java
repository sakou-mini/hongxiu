package com.donglai.model.db.service.sport;

import com.donglai.common.service.RedisService;
import com.donglai.common.util.RandomUtil;
import com.donglai.model.db.entity.sport.SportEvent;
import com.donglai.model.db.repository.sport.SportEventRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

import static com.donglai.common.constant.LiveRedisConstant.SPORT_EVENT;

@Service
public class SportEventService {

    @Autowired
    SportEventRepository repository;
    @Autowired
    RedisService redisService;

    @CachePut(value = SPORT_EVENT, key = "#sportRace.id", unless="#result == null")
    public SportEvent save(SportEvent sportRace){
        return repository.save(sportRace);
    }

    @Cacheable(value = SPORT_EVENT,key="#eventId", unless="#result == null")
    public SportEvent findByEventId(String eventId) {
        return repository.findById(eventId).orElse(null);
    }

    public List<SportEvent> saveAll(Set<SportEvent> raceList){
        redisService.del(Lists.newArrayList(redisService.keys(SPORT_EVENT + "*")));
        return repository.saveAll(raceList);
    }

    @CacheEvict(value = SPORT_EVENT,key = "#sportRace.id",condition = "#sportRace!=null")
    public void delete(SportEvent sportRace){
        repository.delete(sportRace);
    }

    //FIXME DELETE
    public String randomSportEvent(){
        List<SportEvent> sportEvents = repository.findAll();
        if(!CollectionUtils.isEmpty(sportEvents)){
            int index = RandomUtil.getRandomInt(0, sportEvents.size()-1, null);
            return sportEvents.get(index).getId();
        }
        return "";
    }
}
