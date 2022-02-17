package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.system.GameServerConfigDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.database.entity.game.CardGame;
import com.donglaistd.jinli.database.entity.game.EmptyGame;
import com.donglaistd.jinli.database.entity.system.GameServerConfig;
import com.donglaistd.jinli.http.entity.GameServerInfo;
import com.donglaistd.jinli.processors.handler.EndLiveRequestHandler;
import com.donglaistd.jinli.service.statistic.LiveMonitorProcess;
import com.donglaistd.jinli.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class GameServerInfoProcess {
    private Logger logger = Logger.getLogger(GameServerInfoProcess.class.getName());
    @Autowired
    DataManager dataManager;
    @Autowired
    GameServerConfigDaoService gameServerConfigDaoService;
    @Autowired
    EndLiveRequestHandler endLiveRequestHandler;
    @Autowired
    LiveMonitorProcess liveMonitorProcess;

    public GameServerInfo queryGameServerInfo() {
        GameServerConfig serverConfig = gameServerConfigDaoService.findGameServerConfig();
        GameServerConfig.ServerStatue serverStatue = serverConfig.getServerStatue();
        List<CardGame> allGame = DataManager.findAllGame();
        Map<Boolean, List<CardGame>> gameTypeMap = allGame.stream().collect(Collectors.groupingBy(cardGame -> cardGame instanceof BaseGame));
        long raceGameCount = gameTypeMap.getOrDefault(false, new ArrayList<>()).size();
        long liveGameCount = gameTypeMap.getOrDefault(true, new ArrayList<>()).stream().filter(game -> !(game instanceof EmptyGame)).count();
        long liveRoomNum = dataManager.countLiveRoomNum();
        long onlineNum = dataManager.countOnlineNum();
        return new GameServerInfo(serverStatue, onlineNum, liveRoomNum, liveGameCount, raceGameCount);
    }

    public boolean stopGameServer() {
        GameServerConfig serverConfig = gameServerConfigDaoService.findGameServerConfig();
        if (serverConfig == null) return false;
        serverConfig.stopServer();
        gameServerConfigDaoService.saveGameServerConfig(serverConfig);
        logger.info("Stop Server SUCCESS,will close game and close some Handler!");
        closeEmptyGameLiveRoom();
        return true;
    }

    /*close EmptyGame room*/
    private void closeEmptyGameLiveRoom() {
        Set<EmptyGame> emptyGame =
                DataManager.findAllGame().stream().filter(game -> game instanceof EmptyGame).map(game -> (EmptyGame) game).collect(Collectors.toSet());
        LiveUser liveUser;
        for (EmptyGame game : emptyGame) {
            liveUser = game.getOwner();
            liveMonitorProcess.closeLiveRoom(liveUser.getId(), "",0, Constant.EndType.NORMAL_END);
        }
    }

    public boolean resumeGameServer() {
        GameServerConfig serverConfig = gameServerConfigDaoService.findGameServerConfig();
        if (serverConfig == null) return false;
        serverConfig.resumeServer();
        gameServerConfigDaoService.saveGameServerConfig(serverConfig);
        logger.info("Resume Server SUCCESS");
        return true;
    }
}
