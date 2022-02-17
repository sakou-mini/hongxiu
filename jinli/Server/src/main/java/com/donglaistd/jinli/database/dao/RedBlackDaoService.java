package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.game.RedBlack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RedBlackDaoService {
    @Autowired
    RedBlackRepository redBlackRepository;
    public RedBlack getById(String id ){
        return redBlackRepository.findByGameId(id);
    }
    public RedBlack save(RedBlack redBlack){
        return this.redBlackRepository.save(redBlack);
    }
    protected void deleteAll() {
        this.redBlackRepository.deleteAll();
    }
}
