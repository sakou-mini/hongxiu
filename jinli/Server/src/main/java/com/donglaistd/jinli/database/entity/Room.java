package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.chat.MessageRecord;
import com.donglaistd.jinli.util.Pair;
import com.donglaistd.jinli.util.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.constant.GameConstant.DEFAULT_ROOM_IMAGE_PATH;
import static com.donglaistd.jinli.constant.LiveConstant.DEFAULT_LIVE_DOMAIN;
import static com.donglaistd.jinli.util.ComparatorUtil.getPlatformComparator;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;
import static com.donglaistd.jinli.util.MessageUtil.sendMessage;

@Document
public class Room implements Serializable {
    private static final Logger logger = Logger.getLogger(Room.class.getName());
    @Id
    private String id;
    @Field
    @Indexed(unique = true)
    private String displayId;
    @Field
    private String roomTitle;
    @Field
    @Indexed(unique = true)
    private String liveUserId;
    @Field
    private String description;
    @Field
    private String roomImage;
    @Field
    private Date createDate;
    @Field
    private Constant.Pattern pattern = Constant.Pattern.LIVE_VIDEO;
    @Field
    private String userId; //房间所属用户id
    //管理员
    @Field
    private Set<String> administrators = new HashSet<>();
    @Transient
    private Date startDate;
    @Transient
    private long timeToEndLive;  //only used by emptyGame
    @Transient
    private String liveMusic;
    @Transient
    private Constant.LiveSourceLine liveSourceLine;
    @Transient
    private String liveDomain;
    @Transient
    private boolean isHot = true;
    @Transient
    private final Map<Constant.PlatformType, Integer> platformRecommendWeight = new ConcurrentHashMap<>();
    @Transient
    private final Map<Constant.PlatformType, RoomDataClassify> platformRoomData = new ConcurrentHashMap<>();
    @Transient
    private final AtomicBoolean muteAll = new AtomicBoolean(false);
    //连麦申请表
    @Transient
    private final List<Pair<String, String>> connectLiveCode = new CopyOnWriteArrayList<>();
    @Transient
    private String currentConnectLiveUserId = "";
    @Transient
    private final Map<String, Integer> giftRecord = new ConcurrentHashMap<>();
    //房间总价值
    @Transient
    private final AtomicLong totalCount = new AtomicLong(0);

    public Room() {
    }

    public Room(String id, String displayId, String roomTitle, String liveUserId, String description, String userId) {
        this.id = id;
        this.displayId = displayId;
        this.roomTitle = roomTitle;
        this.liveUserId = liveUserId;
        this.description = description;
        this.userId = userId;
        this.createDate = new Date();
    }

    public Room(LiveUser liveUser) {
        this.liveUserId = liveUser.getId();
    }


    public void initPlatformRoomData(Constant.PlatformType platform, String userId, int startFansNum) {
        platformRoomData.put(platform, RoomDataClassify.newInstance(userId, platform, startFansNum));
    }

    public void initPlatformRoomData(Constant.PlatformType platform, String userId, int startFansNum, String platformGameCode, String platformRoomCode) {
        platformRoomData.put(platform, RoomDataClassify.newInstance(userId, platform, startFansNum, platformGameCode, platformRoomCode));
    }

    public List<RoomDataClassify> getAllPlatformData() {
        return new ArrayList<>(platformRoomData.values());
    }

    public RoomDataClassify getPlatformRoomData(Constant.PlatformType platform) {
        if (Objects.isNull(platformRoomData.get(platform))) logger.warning("not share to the platform:" + platform);
        return platformRoomData.getOrDefault(platform, new RoomDataClassify(platform));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addAudience(User user) {
        Constant.PlatformType platform = user.getPlatformType();
        RoomDataClassify platformRoomData = getPlatformRoomData(platform);
        platformRoomData.addAudience(user.getId());
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void removeAudience(User user) {
        RoomDataClassify platformRoomData = getPlatformRoomData(user.getPlatformType());
        platformRoomData.removeAudience(user.getId());
    }

    public Constant.Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Constant.Pattern pattern) {
        this.pattern = pattern;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public void setRoomTitle(String roomName) {
        this.roomTitle = roomName;
    }

    public String getLiveUserId() {
        return liveUserId;
    }

    public void setLiveUserId(String liveUserId) {
        this.liveUserId = liveUserId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String gameInfo) {
        this.description = gameInfo;
    }

    public String getRoomImage() {
        return StringUtils.isNullOrBlank(roomImage) ? DEFAULT_ROOM_IMAGE_PATH : roomImage;
    }

    public void setRoomImage(String roomImg) {
        this.roomImage = roomImg;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getDisplayId() {
        return Strings.isBlank(displayId) ? "" : displayId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void broadCastToAllPlatform(Jinli.JinliMessageReply message) {
        Set<String> allAudience = new HashSet<>();
        platformRoomData.values().stream().map(RoomDataClassify::getAudienceList).forEach(allAudience::addAll);
        allAudience.add(userId);
        allAudience.forEach(userId -> sendMessage(userId, message));
    }

    public void broadCastByUser(Jinli.JinliMessageReply message, User user) {
        if (validateIsLiveUser(user.getLiveUserId())) broadCastToAllPlatform(message);
        else getPlatformRoomData(user.getPlatformType()).broadCast(message);
    }

    public void broadCastToPlatform(Jinli.JinliMessageReply message, Constant.PlatformType platform) {
        getPlatformRoomData(platform).broadCast(message);
    }

    public List<String> getAllPlatformAudienceList() {
        Set<String> allAudience = new HashSet<>();
        platformRoomData.values().stream().map(RoomDataClassify::getAudienceList).forEach(allAudience::addAll);
        return new ArrayList<>(allAudience);
    }

    public List<String> getAudienceByPlatform(Constant.PlatformType platform) {
        return getPlatformRoomData(platform).getAudienceList();
    }

    public boolean notContainsUser(User user) {
        return !getPlatformRoomData(user.getPlatformType()).getAudienceList().contains(user.getId());
    }

    public void setPlatformTotalCount(long totalCount, Constant.PlatformType platform) {
        getPlatformRoomData(platform).setTotalCount(totalCount);
    }

    public void setTotalCoinCount(long totalCount) {
        this.totalCount.set(totalCount);
    }

    public long getPlatformTotalCount(Constant.PlatformType platform) {
        return this.getPlatformRoomData(platform).getTotalCount();
    }

    public long getCoinTotalCount() {
        return this.getAllPlatformData().stream().mapToLong(RoomDataClassify::getTotalCount).sum();
    }

    public long getTotalBetAmount() {
        return this.getAllPlatformData().stream().mapToLong(RoomDataClassify::getTotalBetAmount).sum();
    }

    public long getPlatformBetAmount(Constant.PlatformType platform) {
        return this.getPlatformRoomData(platform).getTotalBetAmount();
    }

    public void addAndGetTotalBetAmount(Constant.PlatformType platform, long add) {
        this.getPlatformRoomData(platform).addAndGetTotalBetAmount(add);
    }

    public List<Pair<String, String>> getConnectLiveCode() {
        return this.connectLiveCode;
    }

    public int getConnectLiveCodeSize() {
        return this.connectLiveCode.size();
    }

    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    public boolean hasConnectLive(String userId) {
        return this.connectLiveCode.stream().map(Pair::getLeft).collect(Collectors.toList()).contains(userId);
    }

    public synchronized void clearConnectLiveCode() {
        this.connectLiveCode.clear();
        platformRoomData.values().forEach(RoomDataClassify::clearCode);
    }

    public synchronized void addConnectLiveCode(String userId, Constant.PlatformType platform) {
        if (getAdministrators().contains(userId)) {
            this.connectLiveCode.add(0, new Pair<>(userId, UUID.randomUUID().toString()));
        } else {
            this.connectLiveCode.add(new Pair<>(userId, UUID.randomUUID().toString()));
        }
        this.getPlatformRoomData(platform).addConnectLiveRecord(userId);
    }

    public synchronized void removeConnectLiveCodeByUser(User user) {
        if (!this.connectLiveCode.removeIf(pair -> Objects.equals(user.getId(), pair.getLeft()))) return;
        if (Objects.equals(currentConnectLiveUserId, user.getId())) cleanCurrentConnectLiveUserId();
        Jinli.RemoveConnectLiveBroadcastMessage.Builder broadcastMessage = Jinli.RemoveConnectLiveBroadcastMessage.newBuilder()
                .setHasOtherConnectLive(!connectLiveCode.isEmpty()).setUserId(user.getId());
        this.broadCastToAllPlatform(buildReply(broadcastMessage.build()));
    }

    public synchronized boolean getMuteAll() {
        return muteAll.get();
    }

    public synchronized void setMuteAll(boolean muteAll) {
        this.muteAll.set(muteAll);
    }


    public Pair<String, String> getConnectLiveCodeByUserId(String userId) {
        return this.connectLiveCode.stream().filter(pair -> Objects.equals(pair.getLeft(), userId)).findFirst().orElse(null);
    }

    public synchronized void cleanCurrentConnectLiveUserId() {
        this.currentConnectLiveUserId = "";
    }

    public synchronized void setCurrentConnectLiveUserId(String currentConnectLiveUserId) {
        this.currentConnectLiveUserId = currentConnectLiveUserId;
    }

    public String getCurrentConnectLiveUserId() {
        return currentConnectLiveUserId;
    }

    public void resetTotalBetAmount() {
        this.getAllPlatformData().forEach(RoomDataClassify::cleanTotalBetAmount);
    }

    public int getHeat() {
        return getAllAudience().size() * 12;
    }


    public Set<String> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(Set<String> administrators) {
        this.administrators = administrators;
    }

    public void addAdministrator(String userId) {
        if (!StringUtils.isNullOrBlank(userId)) {
            this.administrators.add(userId);
        }
    }

    public void revokeAdministrator(String userId) {
        if (!StringUtils.isNullOrBlank(userId)) {
            this.administrators.remove(userId);
        }
    }

    public boolean containsAdministrator(String userId) {
        return this.administrators.contains(userId);
    }

    public boolean validateIsLiveUser(String liveUserId) {
        return Objects.equals(this.liveUserId, liveUserId);
    }


    public synchronized void addChatMessageRecord(MessageRecord messageRecord, Constant.PlatformType platform) {
        if (Objects.equals(messageRecord.getFromUid(), userId)) {
            getAllPlatformData().forEach(platformData -> platformData.addChatMessageRecord(messageRecord));
        } else {
            RoomDataClassify platformRoomData = getPlatformRoomData(platform);
            platformRoomData.addChatMessageRecord(messageRecord);
            platformRoomData.addBulletMessageCount();
        }
    }

    public void cleanRoomChatHistory() {
        getAllPlatformData().forEach(RoomDataClassify::cleanRoomChatHistory);
    }

    public List<MessageRecord> getRoomChatHistory(Constant.PlatformType platform) {
        return getPlatformRoomData(platform).getRoomChatHistory();
    }

    public List<MessageRecord> getRoomAllChatHistory() {
        Set<MessageRecord> messageRecords = new HashSet<>();
        getAllPlatformData().forEach(roomDataClassify -> messageRecords.addAll(roomDataClassify.getRoomChatHistory()));
        return messageRecords.stream().sorted(Comparator.comparing(MessageRecord::getSendTime)).collect(Collectors.toList());
    }

    public int getRoomAllChatUserNum() {
        Set<MessageRecord> messageRecords = new HashSet<>();
        getAllPlatformData().forEach(roomDataClassify -> messageRecords.addAll(roomDataClassify.getRoomChatHistory()));
        return messageRecords.stream().filter(record -> !Objects.equals(record.getFromUid(), userId)).map(MessageRecord::getFromUid).collect(Collectors.toSet()).size();
    }

    public List<MessageRecord> getRoomChatHistoryByUser(User user) {
        if (validateIsLiveUser(user.getLiveUserId())) return getRoomAllChatHistory();
        return getRoomChatHistory(user.getPlatformType());
    }

    public synchronized void updateGiftRank(User sendUser, int sendAmount) {
        getPlatformRoomData(sendUser.getPlatformType()).updateGiftRank(sendUser.getId(), sendAmount);
        giftRecord.put(sendUser.getId(), giftRecord.getOrDefault(sendUser.getId(), 0) + sendAmount);
    }

    public List<Pair<String, Integer>> getTopGiftRank() {
        return giftRecord.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(10)
                .map(e -> new Pair<>(e.getKey(), e.getValue())).collect(Collectors.toList());
    }

    public List<String> getAllAudience() {
        List<String> audienceList = new ArrayList<>();
        getAllPlatformData().forEach(roomDataClassify -> audienceList.addAll(roomDataClassify.getAudienceList()));
        return audienceList;
    }

    public List<String> getAudienceLiveRankIdByLimit(int size) {
        List<String> allAudience = getAllAudience();
        return giftRecord.entrySet().stream().filter(entry -> allAudience.contains(entry.getKey())).sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(size).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public long getTotalGiftCoin() {
        return this.giftRecord.values().stream().mapToLong(Integer::intValue).sum();
    }

    public long getTimeToEndLive() {
        return timeToEndLive;
    }

    public void setTimeToEndLive(long timeToEndLive) {
        this.timeToEndLive = timeToEndLive;
    }

    public String getLiveMusic() {
        return liveMusic == null ? "" : liveMusic;
    }

    public void setLiveMusic(String liveMusic) {
        this.liveMusic = liveMusic;
    }

    public Set<String> getAudienceHistory(Constant.PlatformType platform) {
        return getPlatformRoomData(platform).getAudienceHistory();
    }

    public Set<String> getAllAudienceHistory() {
        Set<String> audienceHistory = new HashSet<>();
        getAllPlatformData().forEach(roomDataClassify -> audienceHistory.addAll(roomDataClassify.getAudienceHistory()));
        return audienceHistory;
    }

    public boolean isHot() {
        return isHot;
    }

    public void setHot(boolean hot) {
        isHot = hot;
    }

    public int getPlatformRecommendWeight(Constant.PlatformType platform){
        return platformRecommendWeight.getOrDefault(platform, 1);
    }

    public void setPlatformRecommendWeight(Constant.PlatformType platform, int recommendWeight) {
        this.platformRecommendWeight.put(platform,recommendWeight);
    }

    public boolean isLive() {
        return getStartDate() != null && timeToEndLive <= 0;
    }

    public Constant.LiveSourceLine getLiveSourceLine() {
        if (Objects.isNull(liveSourceLine)) return Constant.LiveSourceLine.WANGSU_LINE;
        return liveSourceLine;
    }

    public void setLiveSourceLine(Constant.LiveSourceLine liveSourceLine) {
        this.liveSourceLine = liveSourceLine;
    }

    public String getOtherPlatformGameCode(Constant.PlatformType platform) {
        return getPlatformRoomData(platform).getOtherPlatformGameCode();
    }

    public String getOtherPlatformRoomCode(Constant.PlatformType platform) {
        return getPlatformRoomData(platform).getOtherPlatformRoomCode();
    }

    public String getLiveDomain() {
        if (StringUtils.isNullOrBlank(liveDomain)) {
            logger.warning("liveDomain is empty,need chek it ");
            return DEFAULT_LIVE_DOMAIN;
        }
        return liveDomain;
    }

    public void setLiveDomain(String liveDomain) {
        this.liveDomain = liveDomain;
    }

    public long getLiveVisitorCount(Constant.PlatformType platform) {
        return getPlatformRoomData(platform).getLiveVisitorCount();
    }

    public long getBulletMessageCount(Constant.PlatformType platform) {
        return getPlatformRoomData(platform).getBulletMessageCount();
    }

    public long getConnectLiveCount(Constant.PlatformType platform) {
        return getPlatformRoomData(platform).getConnectLiveCount();
    }

    public int getStartOfFansNum(Constant.PlatformType platform) {
        return getPlatformRoomData(platform).getStartOfFansNum();
    }

    public long getGiftCount(Constant.PlatformType platform) {
        return getPlatformRoomData(platform).getGiftCount();
    }

    public void cleanRoom() {
        this.platformRoomData.clear();
        this.giftRecord.clear();
        this.startDate = null;
        this.timeToEndLive = 0;
        cleanCurrentConnectLiveUserId();
    }

    public boolean isSharedToPlatform(Constant.PlatformType platform) {
        return platformRoomData.containsKey(platform);
    }

    public List<Constant.PlatformType> getAllSharedPlatform(){
        return platformRoomData.keySet().stream().sorted(getPlatformComparator()).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", displayId='" + displayId + '\'' +
                ", roomTitle='" + roomTitle + '\'' +
                ", liveUserId='" + liveUserId + '\'' +
                ", roomImage='" + roomImage + '\'' +
                ", createDate=" + createDate +
                ", pattern=" + pattern +
                ", userId='" + userId + '\'' +
                ", startDate=" + startDate +
                ", liveSourceLine=" + liveSourceLine +
                ", liveDomain='" + liveDomain + '\'' +
                ", platformRecommendWeight=" + platformRecommendWeight +
                ", platformRoomData=" + platformRoomData +
                ", giftRecord=" + giftRecord +
                '}';
    }
}
