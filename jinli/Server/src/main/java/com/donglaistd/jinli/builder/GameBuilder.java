package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.*;
import com.donglaistd.jinli.database.entity.LiveGameInfo;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.game.*;
import com.donglaistd.jinli.exception.JinliException;
import com.donglaistd.jinli.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.ResultCode.NO_GAME_TYPE;

@Component
public class GameBuilder {
    private static final Logger logger = Logger.getLogger(GameBuilder.class.getName());

    @Autowired
    private LonghuDaoService longhuDaoService;
    @Autowired
    private BaccaratDaoService baccaratDaoService;
    @Autowired
    private BullBullDaoService bullBullDaoService;
    @Autowired
    private RedBlackDaoService redBlackDaoService;
    @Autowired
    private GoldenFlowerDaoService goldenFlowerDaoService;
    @Autowired
    private EmptyGameDaoService emptyGameDaoService;

    @Autowired
    private LonghuBuilder longhuBuilder;
    @Autowired
    private BaccaratBuilder baccaratBuilder;
    @Autowired
    private BullBullBuilder bullBullBuilder;
    @Autowired
    private RedBlackBuilder redBlackBuilder;
    @Autowired
    private GoldenFlowerBuilder goldenFlowerBuilder;
    @Autowired
    private EmptyGameBuilder emptyGameBuilder;
    @Autowired
    private DataManager dataManager;

    @Value("${game.pay.rate}")
    private String payRate;

    public BaseGame createGame(Constant.GameType gameType, LiveUser liveUser,boolean... isBankerGame) throws JinliException {
        BaseGame game;
        boolean openBanker = false;
        if(isBankerGame.length > 0) openBanker = isBankerGame[0];
        switch (gameType) {
            case LONGHU:
                game = longhuBuilder.create();
                longhuDaoService.save((Longhu) game);
                break;
            case BACCARAT:
                game = baccaratBuilder.create(openBanker);
                baccaratDaoService.save((Baccarat) game);
                break;
            case NIUNIU:
                game = bullBullBuilder.create(openBanker);
                bullBullDaoService.save((BullBull) game);
                break;
            case REDBLACK:
                game = redBlackBuilder.create(openBanker);
                redBlackDaoService.save((RedBlack) game);
                break;
            case GOLDENFLOWER:
                game = goldenFlowerBuilder.create(openBanker);
                goldenFlowerDaoService.save((GoldenFlower) game);
                break;
            case JIAOYOU:
            case MENGCHONG:
            case TIAOWU:
            case CHANGGE:
            case YANZHI:
                game = emptyGameBuilder.create(gameType);
                emptyGameDaoService.save((EmptyGame) game);
                break;
            default:
                throw new JinliException(NO_GAME_TYPE);
        }
        logger.info("created new game:" + game.getGameId() + " of type:" + gameType);
        game.setOwner(liveUser);
        game.setPayRate(new BigDecimal(payRate));
        liveUser.setPlayingGameId(game.getGameId());
        setLiveGameInfo(liveUser,gameType,openBanker,game.getGameId());
        dataManager.saveLiveUser(liveUser);
        return game;
    }

    public void setLiveGameInfo(LiveUser liveUser, Constant.GameType gameType,boolean openBanker,String gameId){
        Room room = DataManager.findOnlineRoom(liveUser.getRoomId());
        if(room != null ){
            LiveGameInfo liveGameInfo = LiveGameInfo.newInstance(gameType, openBanker, gameId);
            List<LiveGameInfo.PlatformRoomParam> platformRoomParams = room.getAllPlatformData().stream().map(roomDataClassify ->
                    new LiveGameInfo.PlatformRoomParam(roomDataClassify.getPlatform(), roomDataClassify.getOtherPlatformGameCode(),
                            roomDataClassify.getOtherPlatformRoomCode())).collect(Collectors.toList());
            liveGameInfo.setPlatformRoomParams(platformRoomParams);
            liveUser.setLiveGameInfo(liveGameInfo);
        }
    }
}
