package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.game.Longhu;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LonghuDaoService {
    @Autowired
    private LonghuRepository longhuRepository;

    @Cacheable(cacheNames = "c1")
    public Longhu save(Longhu longhu) {
        return longhuRepository.save(longhu);
    }

    protected void deleteAll() {
        this.longhuRepository.deleteAll();
    }

    @Cacheable(cacheNames = "c1")
    public Longhu getByGameId(String gameId) {
        return this.longhuRepository.getByGameId(new ObjectId(gameId));
    }
}
