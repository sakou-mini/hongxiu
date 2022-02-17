package com.donglaistd.jinli.config;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.*;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.system.LiveDomainConfigDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.*;
import com.donglaistd.jinli.database.entity.race.GoldenFlowerRace;
import com.donglaistd.jinli.database.entity.race.LandlordsRace;
import com.donglaistd.jinli.database.entity.race.TexasRace;
import com.donglaistd.jinli.exception.JinliException;
import com.donglaistd.jinli.http.service.BackOfficeUserService;
import com.donglaistd.jinli.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.EventListener;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.GameType.*;
import static com.donglaistd.jinli.Constant.LiveStatus.ONLINE;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.constant.GameConstant.DEFAULT_ROOM_IMAGE_PATH;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class GameInit {
    private static final Logger logger = Logger.getLogger(GameInit.class.getName());

    @Value("${data.robot.room.switch}")
    private boolean _switch;
    @Value("${longhu.betting.time}")
    private long LONGHU_BETTING_TIME;

    @Value("${baccarat.betting.time}")
    private long BACCARAT_BETTING_TIME;

    @Value("${bullbull.betting.time}")
    private long BULLBULL_BETTING_TIME;

    @Value("${redblack.betting.time}")
    private long REDBLACK_BETTING_TIME;

    @Value("${goldenflower.betting.time}")
    private long GOLDENFLOWER_BETTING_TIME;
    @Autowired
    GameBuilder gameBuilder;
    @Autowired
    private LiveUserDaoService liveUserDaoService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MongoMappingContext mongoMappingContext;
    @Autowired
    RoomDaoService roomDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    LandlordsRaceBuilder landlordsRaceBuilder;
    @Autowired
    private GoldenFlowerRaceBuilder goldenFlowerRaceBuilder;
    @Autowired
    LiveUserBuilder liveUserBuilder;
    @Autowired
    UserBuilder userBuilder;
    @Autowired
    RoomBuilder roomBuilder;
    @Autowired
    BackOfficeUserService backOfficeUserService;
    @Autowired
    LiveDomainConfigDaoService liveDomainConfigDaoService;

    public void init() {
        Constant.GameType[] types = Arrays.stream(values()).filter(t -> t != UNRECOGNIZED && t != EMPTY && t.getNumber() < LANDLORD_GAME_VALUE).toArray(Constant.GameType[]::new);
        try {
            for (int i = 0; i < types.length; i++) {
                User user = userBuilder.createRegisterUser("test_" + i, "乔碧萝殿下_" + i, "token_test_" + i, 1000000, "", true);
                LiveUser liveUser = liveUserBuilder.create(user.getId(),ONLINE, user.getPlatformType());
                liveUser.setScriptLiveUser(true);
                user.setLiveUserId(liveUser.getId());

                Room room = roomBuilder.create(liveUser.getId(),user.getId(),"title_test_" + i,"description_test_" + i,DEFAULT_ROOM_IMAGE_PATH);
                room.setPattern(Constant.Pattern.LIVE_AUDIO);
                room.setStartDate(new Date());
                liveUser.setRoomId(room.getId());

                user.setCurrentRoomId(room.getId());
                user.setOnline(true);
                dataManager.saveUser(user);
                dataManager.saveRoom(room);
                dataManager.saveLiveUser(liveUser);
                BaseGame game = gameBuilder.createGame(types[i], liveUser);
                DataManager.addGame(game);
                startGame(game);
            }
        } catch (JinliException e) {
            e.printStackTrace();
        }
    }

    public void initRace(){
        initLandLordsRace();
        initTexas();
        initGoldenFlowerRace();
    }

    public void startGame(BaseGame game) {
        if (game.getGameStatus().equals(Constant.GameStatus.PAUSED)) {
            var bm = Jinli.StartGameBroadcastMessage.newBuilder();
            if (game instanceof Longhu) {
                game.beginGameLoop(LONGHU_BETTING_TIME);
                bm.setTimeToEnd(LONGHU_BETTING_TIME);
                bm.setGameType(Constant.GameType.LONGHU);
            } else if (game instanceof Baccarat) {
                game.beginGameLoop(BACCARAT_BETTING_TIME);
                bm.setTimeToEnd(BACCARAT_BETTING_TIME);
                bm.setGameType(Constant.GameType.BACCARAT);
            } else if (game instanceof BullBull) {
                game.beginGameLoop(BULLBULL_BETTING_TIME);
                bm.setTimeToEnd(BULLBULL_BETTING_TIME);
                bm.setGameType(Constant.GameType.NIUNIU);
            } else if (game instanceof RedBlack) {
                game.beginGameLoop(REDBLACK_BETTING_TIME);
                bm.setTimeToEnd(REDBLACK_BETTING_TIME);
                bm.setGameType(Constant.GameType.REDBLACK);
            } else if (game instanceof GoldenFlower) {
                game.beginGameLoop(GOLDENFLOWER_BETTING_TIME);
                bm.setTimeToEnd(GOLDENFLOWER_BETTING_TIME);
                bm.setGameType(Constant.GameType.GOLDENFLOWER);
            }
            var room = DataManager.roomMap.get(game.getOwner().getRoomId());
            bm.setRoomId(room.getId());
            bm.setLeftCardCount(game.getDeckLeftCount());
            bm.setDealtCardCount(game.getDeckDealtCount());
            room.broadCastToAllPlatform(buildReply(bm, SUCCESS));
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initIndicesAfterStartup() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Document.class));
        Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents("com.donglaistd.jinli.database.entity");

        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
        for (var beanDefinition : candidateComponents) {
            String beanClassName = beanDefinition.getBeanClassName();
            logger.info("processing index for class:" + beanClassName);
            try {
                var clazz = Class.forName(beanClassName);
                IndexOperations indexOps = mongoTemplate.indexOps(clazz);
                resolver.resolveIndexFor(clazz).forEach(indexOps::ensureIndex);
            } catch (ClassNotFoundException e) {
                logger.warning("processing index error for class:" + beanClassName);
            }
        }
        initRace();
        if (_switch) init();
        backOfficeUserService.initPlatformAccount();
        liveDomainConfigDaoService.initDomainConfig();
    }

    public void initLandLordsRace() {
        List<LandlordsRace> landlordsRaces = landlordsRaceBuilder.createLandlordRacesLevel(6);
        landlordsRaces.forEach(DataManager::addRace);
    }

    public void initTexas() {
        List<TexasRace> texasRaces = TexasRaceBuilder.createTexasRacesByLevel(6);
        texasRaces.forEach(DataManager::addRace);
    }

    public void initGoldenFlowerRace() {
        List<GoldenFlowerRace> goldenFlowerRaces = goldenFlowerRaceBuilder.createGoldenFlowerRacesByLevel(6);
        goldenFlowerRaces.forEach(DataManager::addRace);
    }

}
