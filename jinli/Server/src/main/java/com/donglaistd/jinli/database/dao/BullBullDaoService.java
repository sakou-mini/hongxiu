package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.constant.CacheNameConstant;
import com.donglaistd.jinli.database.entity.game.BullBull;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BullBullDaoService {

    @Autowired
    BullBullRepository bullBullRepository;

    @CachePut(value = CacheNameConstant.BullBull,key = "#bullBull.getGameId()",condition = "#result!=null")
    public BullBull save(BullBull bullBull){
        return bullBullRepository.save(bullBull);
    }

    @Cacheable(value = CacheNameConstant.BullBull,key = "#gameId")
    public BullBull findById(String gameId){
        return bullBullRepository.findByGameId(new ObjectId(gameId));
    }

    @CacheEvict(value = CacheNameConstant.BullBull,key = "#bullBull.getGameId()")
    @Transactional
    public void deleteBullBull(BullBull bullBull){
        bullBullRepository.delete(bullBull);
    }

    @CacheEvict(value = CacheNameConstant.BullBull)
    @Transactional
    public void deleteAll(){
        bullBullRepository.deleteAll();
    }
}
