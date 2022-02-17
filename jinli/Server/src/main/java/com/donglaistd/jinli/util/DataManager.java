package com.donglaistd.jinli.util;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.ZoneConfigProperties;
import com.donglaistd.jinli.constant.CacheNameConstant;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.PersonDiaryDaoService;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.CardGame;
import com.donglaistd.jinli.database.entity.race.RaceBase;
import com.donglaistd.jinli.database.entity.race.UserRace;
import com.donglaistd.jinli.database.entity.statistic.record.UserRoomRecord;
import com.donglaistd.jinli.redis.codec.StringUserCodec;
import com.google.common.collect.Lists;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.sync.RedisCommands;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.constant.CacheNameConstant.*;
import static com.donglaistd.jinli.processors.handler.MessageHandler.*;

@Component
public class DataManager {
    final public static Map<String, Room> roomMap = new ConcurrentHashMap<>();
    final static public Map<String, CardGame> gameMap = new ConcurrentHashMap<>();
    final static public List<Jinli.Grade> gradeRank = new ArrayList<>();
    final static public List<Jinli.DailyIncome> incomeRank = new ArrayList<>();
    final static public List<Jinli.LiveUserRankInfo> liveRank = new ArrayList<>();
    final static public Map<String, User> disconnectUser = new ConcurrentHashMap<>();
    final static public Map<String, Room> closeRoomInfo = new ConcurrentHashMap<>();
    public static final Map<String, Channel> userChannel = new ConcurrentHashMap<>();
    final private static Map<String, LiveUser> liveUserMap = new ConcurrentHashMap<>();
    private static Pair<Long, List<String>> passDiaryIds = null;
    private static final Map<String, UserRace> userRaceMap = new ConcurrentHashMap<>();
    private static final Map<Constant.RaceType, Map<String, RaceBase>> raceMap = new ConcurrentHashMap<>();
    private static final Map<String, Object> lockUser = new ConcurrentHashMap<>();
    private static final Logger logger = Logger.getLogger(DataManager.class.getName());
    private static final Map<String, FutureTaskWeakSet> userEndLiveTask = new ConcurrentHashMap<>();
    private static final Map<String, Long> enterRoomRecords = new ConcurrentHashMap<>();

    private static final CopyOnWriteArraySet<Channel> httpChannel = new CopyOnWriteArraySet<>(); //use to backoffic

    private static final Map<String, UserRoomRecord> userRoomRecords = new ConcurrentHashMap<>();

    private static final Map<String, FutureTaskWeakSet> userDisconnectTask = new ConcurrentHashMap<>();

    @Autowired
    RedisTemplate<String, User> userTemplate;
    @Autowired
    RedisTemplate<String, LiveUser> liveUserTemplate;
    @Autowired
    RedisTemplate<String, String> stringRedisTemplate;
    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    private LiveUserDaoService liveUserDaoService;
    @Autowired
    private RoomDaoService roomDaoService;
    @SuppressWarnings("rawtypes")
    private RedisCommands redisCommands;
    @Autowired
    PersonDiaryDaoService personDiaryDaoService;
    @Autowired
    ZoneConfigProperties zoneConfigProperties;

    @Value("${spring.redis.nodes}")
    private String[] redisNode;

    static public Room findOnlineRoom(String id) {
        return roomMap.get(id);
    }

    static public Room findRoomByLiveUserId(String liveUserId) {
        return roomMap.values().stream().filter(room -> room.getLiveUserId().equals(liveUserId)).findFirst()
                .orElse(closeRoomInfo.values().stream().filter(room -> room.getLiveUserId().equals(liveUserId)).findFirst().orElse(null));
    }
    static public Collection<Room> getOnlineRoomList(){
        return roomMap.values();
    }

    static public void removeOnlineRoom(String roomId){
        roomMap.remove(roomId);
    }

    static public Room getRoomFromChannel(ChannelHandlerContext ctx) {
        return findOnlineRoom(ctx.channel().attr(ROOM_KEY).get());
    }

    static public void saveRoomKeyToChannel(ChannelHandlerContext ctx, String roomId) {
        ctx.channel().attr(ROOM_KEY).set(roomId);
    }

    static public void saveUserKeyToChannel(ChannelHandlerContext ctx, String userId) {
        ctx.channel().attr(USER_KEY).set(userId);
    }

    static public void saveDomainKeyToChannel(ChannelHandlerContext ctx, String domainName) {
        ctx.channel().attr(DOMAIN_KEY).set(domainName);
    }

    static public void saveRealIpToChannel(ChannelHandlerContext ctx, String ip) {
        if(StringUtils.isNullOrBlank(ip)) return;
        ctx.channel().attr(IP_KEY).set(ip);
    }

    static public String getDomainKeyFromChannel(Channel channel) {
        Attribute<String> attr = channel.attr(DOMAIN_KEY);
        if(attr !=null) return attr.get();
        return null;
    }

    static public String getRealIpKeyFromChannel(Channel channel) {
        Attribute<String> attr = channel.attr(IP_KEY);
        if(attr !=null) return attr.get();
        return null;
    }

    public static Channel getUserChannel(String userId) {
        return userChannel.get(userId);
    }

    public synchronized User saveUser(User user) {
        String cacheKey = getUserCacheKey(user.getId());
        userTemplate.opsForValue().set(cacheKey, user);
        return userDaoService.save(user);
    }

    public synchronized User findOnlineUser(String id) {
        String key = getUserCacheKey(id);
        return userTemplate.opsForValue().get(key);
    }

    public synchronized User findUser(String id) {
        try {
            String cacheKey = getUserCacheKey(id);
            logger.finer("find user:" + id);
            var user = userTemplate.opsForValue().get(cacheKey);
            if (user != null) {
                return user;
            }
            user = userDaoService.findById(id);
            userTemplate.opsForValue().set(cacheKey, user);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            logger.warning(e.getMessage());
            return null;
        }
    }

    public void clearRedisAll() {
        //Boolean cleanData = stringRedisTemplate.opsForValue().setIfAbsent("cleanData", "1", 20000, TimeUnit.MILLISECONDS);
        Set<String> keys = stringRedisTemplate.keys("*");
        List<String> delKes = keys.stream().filter(key -> !Objects.equals(key, LOCK_SERVER)).collect(Collectors.toList());
        stringRedisTemplate.delete(delKes);
       /* if(cleanData){
            stringRedisTemplate.delete(keys);
        }else{
            List<String> delKes = keys.stream().filter(key -> !Objects.equals(key, LOCK_SERVER)).collect(Collectors.toList());
            stringRedisTemplate.delete(delKes);
        }*/

    }

    //for test only
    private void clearAll() {
        if (redisCommands == null) {
            var uri = RedisURI.create("redis://" + redisNode[0].split(":")[0] + ":" + redisNode[0].split(":")[1]);
            var codec = new StringUserCodec();
            redisCommands = RedisClient.create(uri).connect(codec).sync();
        }

        roomMap.clear();
        gameMap.clear();
        gradeRank.clear();
        incomeRank.clear();
        liveRank.clear();
        closeRoomInfo.clear();
        redisCommands.flushall();
    }

    public User getUserFromChannel(ChannelHandlerContext ctx) {
        var userIdAttribute = ctx.channel().attr(USER_KEY);
        if (userIdAttribute == null) {return null;}
        var id = userIdAttribute.get();
        if (id == null) return null;
        return findUser(id);
    }

    public void removeUser(User user) {
        String key = getUserCacheKey(user.getId());
        userTemplate.delete(key);
    }

    public LiveUser findLiveUser(String id) {
        if (Strings.isBlank(id)) return null;
        try {
            LiveUser liveUser = liveUserMap.get(id);
            if (liveUser != null) {
                return liveUser;
            }
            String cacheKey = getLiveUserCacheKey(id);
            liveUser = liveUserTemplate.opsForValue().get(cacheKey);
            if (liveUser != null) {
                liveUserMap.put(id, liveUser);
                return liveUser;
            }
            liveUser = liveUserDaoService.findById(id);
            if(liveUser == null) return null;
            liveUserMap.put(id, liveUser);
            liveUserTemplate.opsForValue().set(cacheKey, liveUser);
            return liveUser;
        } catch (Exception e) {
            e.printStackTrace();
            logger.warning("redis cant not connected :" + e.getMessage());
            return null;
        }
    }

    public void saveLiveUser(LiveUser liveUser) {
        if(Objects.isNull(liveUser)) return;
        liveUserMap.put(liveUser.getId(), liveUser);
        String cacheKey = getLiveUserCacheKey(liveUser.getId());
        liveUserTemplate.opsForValue().set(cacheKey, liveUser);
        liveUserDaoService.save(liveUser);
    }

    public void removeLiveUser(LiveUser liveUser) {
        liveUserMap.remove(liveUser.getId());
        liveUserTemplate.delete(liveUser.getId());
    }

    public synchronized Boolean checkLiveUser(String id) {
        return liveUserMap.containsKey(id);
    }

    public synchronized LiveUser randomLiveUser(Constant.PlatformType platformType,Set<String> excludingLiveUserIds) {
        List<LiveUser> onlineLiveUsers = liveUserMap.values().stream().filter(liveUser -> !excludingLiveUserIds.contains(liveUser.getId())
                && liveUser.getLiveStatus().equals(Constant.LiveStatus.ONLINE)
                && Objects.equals(platformType,liveUser.getPlatformType())
                && !StringUtils.isNullOrBlank(liveUser.getPlayingGameId())).collect(Collectors.toList());
        if (onlineLiveUsers.isEmpty()) return null;
        var index = new Random().nextInt(onlineLiveUsers.size());
        return onlineLiveUsers.get(index);
    }

    public synchronized void saveRoom(Room room) {
        roomMap.put(room.getId(), room);
        roomDaoService.save(room);
    }

    public void putUserChannel(String uid, Channel channel) {
        userChannel.put(uid, channel);
    }

    public Channel removeUserChannel(String userId) {
        logger.warning("remove user channel for memory----> for user"+userId);
        return userChannel.remove(userId);
    }

    public static void updatePassDiaryIdsMap(List<String> ids) {
        passDiaryIds = new Pair<>(System.currentTimeMillis(), ids);
    }

    public void removeDiaryFromDiaryIds(String diaryId) {
        if (Objects.nonNull(passDiaryIds)) {
            passDiaryIds.getRight().remove(diaryId);
        }
    }

    public List<String> getPassDiaryIds() {
        List<String> ids;
        if (Objects.isNull(passDiaryIds) || ((System.currentTimeMillis() - passDiaryIds.getLeft()) > zoneConfigProperties.getDiaryIdExpireTime())) {
            ids = personDiaryDaoService.findAllApprovalPassIdsLimit(zoneConfigProperties.getDiaryTopicNum());
            if (ids.isEmpty()) return new ArrayList<>();
            DataManager.updatePassDiaryIdsMap(ids);
        }
        return passDiaryIds.getRight();
    }

    public static UserRace findUserRace(String userId) {
        return userRaceMap.get(userId);
    }

    public static void saveUserRace(String userId, UserRace userRace) {
        userRaceMap.put(userId, userRace);
    }

    public static void removeUserRace(String userId) {
        userRaceMap.remove(userId);
    }

    public static void addRace(RaceBase race) {
        Constant.RaceType raceType = race.getRaceType();
        Map<String, RaceBase> races = raceMap.computeIfAbsent(raceType, k -> new HashMap<>());
        races.put(race.getId(), race);
    }

    public static RaceBase findRace(String raceId) {
        RaceBase raceBase = null;
        for (Map.Entry<Constant.RaceType, Map<String, RaceBase>> raceEntry : raceMap.entrySet()) {
            raceBase = raceEntry.getValue().get(raceId);
            if (raceBase != null) break;
        }
        return raceBase;
    }

    public static List<RaceBase> getAllRaceByRaceType(Constant.RaceType type) {
        Map<String, RaceBase> races = raceMap.getOrDefault(type, new HashMap<>());
        return Lists.newArrayList(races.values());
    }

    public static Map<Constant.RaceType, Map<String, RaceBase>> getRaceMap() {
        return raceMap;
    }

    public static void removeRace(String raceId) {
        Map<String, RaceBase> race;
        for (Map.Entry<Constant.RaceType, Map<String, RaceBase>> raceEntry : raceMap.entrySet()) {
            race = raceEntry.getValue();
            if (race.remove(raceId) != null) break;
        }
    }

    public static void addGame(CardGame game) {
        gameMap.put(game.getGameId(), game);
    }

    public static void removeGame(String id) {
        gameMap.remove(id);
    }

    public static CardGame findGame(String gameId) {
        if(StringUtils.isNullOrBlank(gameId)) return null;
        return gameMap.get(gameId);
    }

    public static List<CardGame> findAllGame() {
        return new ArrayList<>(gameMap.values());
    }

    public static void cleanGame() {
        gameMap.clear();
    }

    public static Object getUserLock(String userId) {
        return lockUser.getOrDefault(userId, new Object());
    }

    public static void removeUserLock(String userId) {
        lockUser.remove(userId);
    }

    public static void addUserEndLiveTask(String userId, ScheduledFuture<?> future) {
        userEndLiveTask.compute(userId, (k, v) -> {
            if (v == null) {
                v = new FutureTaskWeakSet();
            }
            v.add(future);
            return v;
        });
    }

    public static boolean removeEndLiveTask(String userId) {
        FutureTaskWeakSet taskWeakSet = userEndLiveTask.get(userId);
        if (Objects.nonNull(taskWeakSet)) {
            taskWeakSet.clear();
            return true;
        }
        return false;
    }

    public long countLiveRoomNum() {
        return roomMap.values().stream().filter(room -> room.getStartDate() != null).count();
    }

    public long countOnlineNum() {
        return userChannel.values().size();
    }

    public String getUserRemoteAddress(String userId){
        Channel userChannel = getUserChannel(userId);
        if(userChannel == null) return "";
        String ip = getRealIpKeyFromChannel(userChannel);
        if(StringUtils.isNullOrBlank(ip)){
            InetSocketAddress insocket = (InetSocketAddress) userChannel.remoteAddress();
            ip = insocket == null ? "" : insocket.getAddress().getHostAddress();
        }
        return ip;
    }

    public void saveUserEnterRoomRecord(String userId,String liveUserId){
        String key = CacheNameConstant.getEnterRoomRecordKey(userId);
        stringRedisTemplate.opsForValue().set(key, liveUserId);
    }
    public void removeEnterRoomRecord(String userId){
        String key = CacheNameConstant.getEnterRoomRecordKey(userId);
        stringRedisTemplate.delete(key);
    }
    public String getUserEnterRoomRecord(String userId){
        String key = CacheNameConstant.getEnterRoomRecordKey(userId);
        return stringRedisTemplate.opsForValue().get(key);
    }

    public static void saveRoomIdToHttpChannel(ChannelHandlerContext ctx, String roomId){
        saveRoomKeyToChannel(ctx, roomId);
        httpChannel.add(ctx.channel());
    }

    public static void removeHttpChannel(Channel channel){
        httpChannel.remove(channel);
    }
    public static Set<Channel> getHttpChannel(){
        return httpChannel;
    }

    //=============totalLiveWatchRecord==============
    public static void saveUserRoomRecord(String userId, UserRoomRecord userRoomRecord){
        userRoomRecords.put(userId, userRoomRecord);
    }

    public static UserRoomRecord getUserRoomRecord(String userId) {
        return userRoomRecords.getOrDefault(userId,new UserRoomRecord());
    }

    public static void cleanUserRoomRecord(String userId){
        userRoomRecords.remove(userId);
    }

    public static void addUserDisconnectTask(String userId, ScheduledFuture<?> future) {
        userDisconnectTask.compute(userId, (k, v) -> {
            if (v == null) v = new FutureTaskWeakSet();
            v.add(future);
            return v;
        });
    }

    public static boolean removeUserDisconnectTask(String userId) {
        FutureTaskWeakSet taskWeakSet = userDisconnectTask.get(userId);
        if (Objects.nonNull(taskWeakSet)) {
            taskWeakSet.clear();
            return true;
        }
        return false;
    }
}
