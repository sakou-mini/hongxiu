package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.constant.CacheNameConstant;
import com.donglaistd.jinli.database.entity.game.GoldenFlower;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoldenFlowerDaoService {
    @Autowired
    GoldenFlowerRepository goldenFlowerRepository;

    @Transactional
    @CachePut(value = CacheNameConstant.GoldenFlower,key = "#goldenFlower.getGameId()",condition = "#result!=null")
    public GoldenFlower save(GoldenFlower goldenFlower){
        return goldenFlowerRepository.save(goldenFlower);
    }

    @Cacheable(value = CacheNameConstant.GoldenFlower,key = "#gameId")
    public GoldenFlower findById(String gameId){
        return goldenFlowerRepository.findByGameId(new ObjectId(gameId));
    }

    @CacheEvict(value = CacheNameConstant.GoldenFlower,key = "#goldenFlower.getGameId()")
    @Transactional
    public void deleteGoldenFlower(GoldenFlower goldenFlower){
        goldenFlowerRepository.delete(goldenFlower);
    }

    @CacheEvict(value = CacheNameConstant.GoldenFlower)
    @Transactional
    public void deleteAll(){
        goldenFlowerRepository.deleteAll();
    }
}
