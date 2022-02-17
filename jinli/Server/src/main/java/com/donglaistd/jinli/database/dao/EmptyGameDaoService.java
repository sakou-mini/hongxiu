package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.game.EmptyGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmptyGameDaoService {
    @Autowired
    EmptyGameRepository emptyGameRepository;

    @Transactional
    public EmptyGame save(EmptyGame emptyGame){
        return emptyGameRepository.save(emptyGame);
    }

    public EmptyGame findById(String gameId){
        return emptyGameRepository.findByGameId((gameId));
    }

    @Transactional
    public void deleteGoldenFlower(EmptyGame emptyGame){
        emptyGameRepository.delete(emptyGame);
    }

    @Transactional
    public void deleteAll(){
        emptyGameRepository.deleteAll();
    }
}
