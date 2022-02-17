package com.donglaistd.jinli.database.dao.system;

import com.donglaistd.jinli.database.entity.system.GameServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.donglaistd.jinli.constant.GameConstant.SERVER_ID;

@Service
public class GameServerConfigDaoService {
    @Autowired
    GameServerConfigRepository gameServerConfigRepository;

    public void initGameServer(){
        Optional<GameServerConfig> result = gameServerConfigRepository.findById(SERVER_ID);
        if(result.isEmpty()){
            GameServerConfig gameServerConfig = GameServerConfig.newInstance(SERVER_ID);
            gameServerConfigRepository.save(gameServerConfig);
        }
    }

    public GameServerConfig findGameServerConfig(){
        return gameServerConfigRepository.findById(SERVER_ID).orElse(null);
    }

    public void saveGameServerConfig(GameServerConfig gameServerConfig){
        gameServerConfigRepository.save(gameServerConfig);
    }
}

