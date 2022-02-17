package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.chat.MessageRecord;
import com.donglaistd.jinli.util.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.constant.GameConstant.ROOM_CHAT_MAX_NUM;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;
import static com.donglaistd.jinli.util.MessageUtil.sendMessage;

public class RoomDataClassify {
    //主播平台
    private String liveOfUserId;
    private Constant.PlatformType platform;
    //观众席(不含主播)
    private final Set<String> audienceList = new CopyOnWriteArraySet<>();
    //房间总价值
    private final AtomicLong totalCount = new AtomicLong(0);
    //总下注金额
    private AtomicLong totalBetAmount = new AtomicLong(0);
    //连麦申请表 TODO DELETE
    private List<Pair<String, String>> connectLiveCode = new CopyOnWriteArrayList<>();
    //房间聊天记录
    private Vector<MessageRecord> roomChatHistory = new Vector<>(ROOM_CHAT_MAX_NUM);
    //当前直播礼物排行
    private final Map<String, Integer> liveRank = new ConcurrentHashMap<>();
    //观众历史
    private final Set<String> audienceHistory = new CopyOnWriteArraySet<>();
    //平台方游戏code
    private String otherPlatformGameCode;
    //其他平台方房间code
    private String otherPlatformRoomCode;
    //直播间访问次数
    private final AtomicLong liveVisitorCount = new AtomicLong(0);
    //弹幕条数
    private final AtomicLong  bulletMessageCount = new AtomicLong(0);
    //连麦次数
    private final AtomicLong  connectLiveCount = new AtomicLong(0);
    //开始直播粉丝数
    private int startOfFansNum;
    //礼物打赏次数
    private final AtomicLong giftCount = new AtomicLong(0);

    public RoomDataClassify(Constant.PlatformType platform) {
        this.platform = platform;
    }

    public RoomDataClassify(String liveOfUserId, Constant.PlatformType platform, int startOfFansNum) {
        this.liveOfUserId = liveOfUserId;
        this.platform = platform;
        this.startOfFansNum = startOfFansNum;
    }

    public RoomDataClassify(String liveOfUserId, Constant.PlatformType platform,int startOfFansNum, String otherPlatformGameCode, String otherPlatformRoomCode) {
        this.liveOfUserId = liveOfUserId;
        this.platform = platform;
        this.startOfFansNum = startOfFansNum;
        this.otherPlatformGameCode = otherPlatformGameCode;
        this.otherPlatformRoomCode = otherPlatformRoomCode;
    }

    public static RoomDataClassify newInstance(String liveOfUserId, Constant.PlatformType platform, int startOfFansNum){
        return new RoomDataClassify(liveOfUserId, platform, startOfFansNum);
    }

    public static RoomDataClassify newInstance(String liveOfUserId, Constant.PlatformType platform, int startOfFansNum, String otherPlatformGameCode, String otherPlatformRoomCode){
        return new RoomDataClassify(liveOfUserId, platform, startOfFansNum, otherPlatformGameCode, otherPlatformRoomCode);
    }

    public String getLiveOfUserId() {
        return liveOfUserId;
    }

    public void setLiveOfUserId(String liveOfUserId) {
        this.liveOfUserId = liveOfUserId;
    }

    public Constant.PlatformType getPlatform() {
        return platform;
    }

    public void setPlatform(Constant.PlatformType platform) {
        this.platform = platform;
    }

    public long getTotalBetAmount() {
        return totalBetAmount.get();
    }

    public void setTotalBetAmount(AtomicLong totalBetAmount) {
        this.totalBetAmount = totalBetAmount;
    }

    public void cleanTotalBetAmount() {
        setTotalBetAmount(new AtomicLong(0));
    }

    public void setConnectLiveCode(List<Pair<String, String>> connectLiveCode) {
        this.connectLiveCode = connectLiveCode;
    }

    public Vector<MessageRecord> getRoomChatHistory() {
        return roomChatHistory;
    }

    public void setRoomChatHistory(Vector<MessageRecord> roomChatHistory) {
        this.roomChatHistory = roomChatHistory;
    }

    public Set<String> getAudienceHistory() {
        return audienceHistory;
    }

    public String getOtherPlatformGameCode() {
        return otherPlatformGameCode;
    }

    public void setOtherPlatformGameCode(String otherPlatformGameCode) {
        this.otherPlatformGameCode = otherPlatformGameCode;
    }

    public String getOtherPlatformRoomCode() {
        return otherPlatformRoomCode;
    }

    public void setOtherPlatformRoomCode(String otherPlatformRoomCode) {
        this.otherPlatformRoomCode = otherPlatformRoomCode;
    }

    public int getStartOfFansNum() {
        return startOfFansNum;
    }

    public void setStartOfFansNum(int startOfFansNum) {
        this.startOfFansNum = startOfFansNum;
    }

    public long getTotalGiftCoin() {
        return liveRank.values().stream().mapToInt(Integer::intValue).sum();
    }

    public long getLiveVisitorCount() {
        return liveVisitorCount.get();
    }

    public void addLiveVisitorCount(){
        liveVisitorCount.incrementAndGet();
    }

    public void addBulletMessageCount(){
        bulletMessageCount.incrementAndGet();
    }

    public long getBulletMessageCount() {
        return bulletMessageCount.get();
    }

    public long getConnectLiveCount() {
        return connectLiveCount.get();
    }

    public void addGiftCount(){
        giftCount.incrementAndGet();
    }
    public long getGiftCount() {
        return giftCount.get();
    }

    //---------------------------------------------------------
    public void addAudience(String userId) {
        if (!Objects.equals(userId,liveOfUserId )) {
            audienceList.add(userId);
            audienceHistory.add(userId);
            addLiveVisitorCount();
        }
    }

    public void removeAudience(String userId) {
        audienceList.remove(userId);
    }

    public List<String> getAudienceList() {
        return new ArrayList<>(audienceList);
    }


    public boolean audiencesContains(String userId) {
        if(Objects.equals(userId,liveOfUserId)) return true;
        return !audienceList.contains(userId);
    }

    public void setTotalCount(long totalCount){
        this.totalCount.set(totalCount);
    }

    public long getTotalCount() {
        return this.totalCount.get();
    }

    public void addAndGetTotalBetAmount(long add) {
        this.totalBetAmount.addAndGet(add);
    }

    public List<Pair<String, String>> getConnectLiveCode() {
        return connectLiveCode;
    }

    public boolean hasConnectLive(String userId) {
        return this.connectLiveCode.stream().map(Pair::getLeft).collect(Collectors.toList()).contains(userId);
    }

    public synchronized void clearCode() {
        this.connectLiveCode.clear();
    }

    public void addConnectLiveCount() {
        this.connectLiveCount.incrementAndGet();
    }

    public synchronized void addConnectLiveRecord(String userId) {
        addConnectLiveCount();
        this.connectLiveCode.add(new Pair<>(userId, UUID.randomUUID().toString()));
    }

    public synchronized void removeConnectLiveCodeByUser(String userId) {
        if (Objects.isNull(userId)) return;
        if(!this.connectLiveCode.removeIf(pair -> Objects.equals(userId, pair.getLeft()))){
            return;
        }
        Jinli.RemoveConnectLiveBroadcastMessage.Builder builder = Jinli.RemoveConnectLiveBroadcastMessage.newBuilder();
        builder.setHasOtherConnectLive(!connectLiveCode.isEmpty());
        this.broadCast(buildReply(builder.setUserId(userId).build()));
    }


    public void broadCast(Jinli.JinliMessageReply message) {
        for (String userId : audienceList) {
            sendMessage(userId, message);
        }
        sendMessage(liveOfUserId, message);
    }

    public Pair<String, String> getConnectLiveCodeByUserId(String userId) {
        for (Pair<String, String> pair : this.connectLiveCode) {
            if (Objects.equals(userId, pair.getLeft()))
                return pair;
        }
        return null;
    }

    public int getHeat() {
        return this.audienceList.size() * 12;
    }

    public synchronized void addChatMessageRecord(MessageRecord messageRecord) {
        if (roomChatHistory.size() >= ROOM_CHAT_MAX_NUM) {
            roomChatHistory.remove(0);
        }
        roomChatHistory.add(messageRecord);
    }

    public void cleanRoomChatHistory() {
        roomChatHistory.clear();
    }

    public synchronized void updateGiftRank(String sendUserId, int sendAmount) {
        addGiftCount();
        liveRank.put(sendUserId, liveRank.getOrDefault(sendUserId, 0) + sendAmount);
    }

    /*public List<Pair<String,Integer>> getSortedLiveRank(){
      return liveRank.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(10)
              .map(e -> new Pair<>(e.getKey(), e.getValue())).collect(Collectors.toList());
    }*/

   /* public List<String> getAudienceLiveRankIdByLimit(int size){
        return liveRank.entrySet().stream().filter(entry -> audienceList.contains(entry.getKey())).sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(size).map(Map.Entry::getKey).collect(Collectors.toList());
    }*/
}
