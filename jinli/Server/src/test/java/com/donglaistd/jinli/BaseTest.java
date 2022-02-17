package com.donglaistd.jinli;

import com.donglaistd.jinli.builder.LiveUserBuilder;
import com.donglaistd.jinli.builder.RoomBuilder;
import com.donglaistd.jinli.builder.UserBuilder;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.kafka.KafkaMessageHandler;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.service.ServerAvailabilityCheckService;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.ScheduledTaskUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.LiveStatus.ONLINE;
import static com.donglaistd.jinli.Constant.PlatformType.PLATFORM_JINLI;
import static com.donglaistd.jinli.constant.GameConstant.DEFAULT_ROOM_IMAGE_PATH;
import static com.donglaistd.jinli.processors.handler.MessageHandler.ROOM_KEY;
import static com.donglaistd.jinli.processors.handler.MessageHandler.USER_KEY;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseTest {
    private static final Logger logger = Logger.getLogger(BaseTest.class.getName());

    @SpyBean
    protected KafkaMessageHandler kafkaMessageHandler;

    protected ChannelHandlerContext context;
    protected Channel channel;
    protected Attribute<String> attributeUser;
    protected Attribute<String> attributeRoom;
    protected User user;
    protected LiveUser liveUser;
    protected Room room;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    protected DataManager dataManager;
    @Autowired
    public LiveUserBuilder liveUserBuilder;
    @Autowired
    public RoomBuilder roomBuilder;
    @Autowired
    public UserBuilder userBuilder;
    @Autowired
    protected UserDaoService userDaoService;
    @Autowired
    ServerAvailabilityCheckService serverAvailabilityCheckService;

    @Before
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        clearAllData();
        serverAvailabilityCheckService.startCheckServer();
        context = Mockito.mock(ChannelHandlerContext.class);
        channel = Mockito.mock(Channel.class);
        attributeUser = Mockito.mock(Attribute.class);
        attributeRoom = Mockito.mock(Attribute.class);
        Mockito.when(context.channel()).thenReturn(channel);
        Mockito.when(channel.attr(USER_KEY)).thenReturn(attributeUser);
        Mockito.when(channel.attr(ROOM_KEY)).thenReturn(attributeRoom);
        user = userBuilder.createNoSavedUser("accountName", "displayName", "", "mock_token", 0, "1234567890", true);
        user.increaseModifyNameCount();
        user.setTourist(false);

        liveUser = liveUserBuilder.create(user.getId(), ONLINE,  PLATFORM_JINLI);
        liveUser.setLevel(1);

        Mockito.when(attributeUser.get()).thenReturn(user.getId());
        Mockito.when(context.channel()).thenReturn(channel);
        room = roomBuilder.create(liveUser.getId(), user.getId(), "room_title", "room description", DEFAULT_ROOM_IMAGE_PATH);
        room.initPlatformRoomData(PLATFORM_JINLI,user.getId(),0);
        room.setStartDate(new Date());
        liveUser.setRoomId(room.getId());
        user.setLiveUserId(liveUser.getId());
        user.setCurrentRoomId(room.getId());
        Mockito.when(attributeRoom.get()).thenReturn(room.getId());

        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        dataManager.saveRoom(room);
        DataManager.roomMap.put(room.getId(), room);
    }

    @After
    public void tearDown() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        clearAllData();
    }

    protected void clearAllData() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        for (var game : DataManager.gameMap.values()) {
            if (game instanceof BaseGame) forceStop((BaseGame) game);
        }
        var clearAllFunction = DataManager.class.getDeclaredMethod("clearAll");
        clearAllFunction.setAccessible(true);
        clearAllFunction.invoke(dataManager);
        EventPublisher.isEnabled = new AtomicBoolean(false);
        for (String name : cacheManager.getCacheNames()) {
            Objects.requireNonNull(cacheManager.getCache(name)).clear();
        }
        for (var collectionName : mongoTemplate.getCollectionNames()) {
            var collection = mongoTemplate.getCollection(collectionName);
            collection.deleteMany(new Document());
        }
    }

    protected void forceStop(BaseGame baseGame) {
        try {
            var field = ScheduledTaskUtil.class.getDeclaredField("scheduledThreadPoolExecutor");
            field.setAccessible(true);
            var modifiersField = field.getClass().getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) field.get(baseGame);
            executor.shutdownNow();
            field.set(null, new ScheduledThreadPoolExecutor(4));
            logger.warning("stopped game:" + baseGame.getGameId());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.warning("stopping game failed");
            e.printStackTrace();
        }
    }

    protected User createTester(long coinCount, String name) {
        var user = userBuilder.createNoSavedUser(name, name, "");
        user.setGameCoin(coinCount);
        dataManager.saveUser(user);
        return user;
    }
}
