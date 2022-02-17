package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.game.Baccarat;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BaccaratDaoService {
    @Autowired
    private BaccaratRepository repository;

    @Cacheable(cacheNames = "c1")
    public Baccarat save(Baccarat game) {
        return repository.save(game);
    }

    protected void deleteAll() {
        this.repository.deleteAll();
    }

    @Cacheable(cacheNames = "c1")
    public Baccarat getByGameId(String gameId) {
        return this.repository.getByGameId(new ObjectId(gameId));
    }
}
