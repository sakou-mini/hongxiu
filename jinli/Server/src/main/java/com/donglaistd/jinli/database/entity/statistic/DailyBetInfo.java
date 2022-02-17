package com.donglaistd.jinli.database.entity.statistic;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;


@Document(collection = "dailyBetInfo")
public class DailyBetInfo {
    @Id
    private String id = ObjectId.get().toString();
    private String liveUserId;
    private String betUserId;
    private long time;
    private long betAmount;
    private long win;
    private String roomId;
    private String roomDisplayId;
    private Constant.GameType gameType;
    private Map<Game.BetType, Long> betRecord;
    private List<Game.BetType> gameResult;
    private long userCoin;
    private boolean isBankerGame= false;
    @Transient
    private String displayName;
    @Transient
    private String avatarUrl;

    public DailyBetInfo(String liveUserId, String betUserId, long time, int betAmount) {
        this.liveUserId = liveUserId;
        this.betUserId = betUserId;
        this.time = time;
        this.betAmount = betAmount;
    }

    public DailyBetInfo() {
    }

    public DailyBetInfo(String liveUserId, String betUserId, long time, long betAmount, long win, String roomId, Constant.GameType gameType) {
        this.liveUserId = liveUserId;
        this.betUserId = betUserId;
        this.time = time;
        this.betAmount = betAmount;
        this.win = win;
        this.roomId = roomId;
        this.gameType = gameType;
    }

    public DailyBetInfo(String liveUserId, String betUserId, long time, long betAmount, long win, String roomId, Constant.GameType gameType,
                        Map<Game.BetType, Long> betRecord, List<Game.BetType> gameResult, long userCoin,boolean isBankerGame,String roomDisplayId) {
        this.liveUserId = liveUserId;
        this.betUserId = betUserId;
        this.time = time;
        this.betAmount = betAmount;
        this.win = win;
        this.roomId = roomId;
        this.gameType = gameType;
        this.betRecord = betRecord;
        this.gameResult = gameResult;
        this.userCoin = userCoin;
        this.isBankerGame = isBankerGame;
        this.roomDisplayId = roomDisplayId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public long getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLiveUserId() {
        return liveUserId;
    }

    public void setLiveUserId(String liveUserId) {
        this.liveUserId = liveUserId;
    }

    public String getBetUserId() {
        return betUserId;
    }

    public void setBetUserId(String betUserId) {
        this.betUserId = betUserId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(int betAmount) {
        this.betAmount = betAmount;
    }

    public Constant.GameType getGameType() {
        return gameType;
    }

    public void setGameType(Constant.GameType gameType) {
        this.gameType = gameType;
    }

    public Map<Game.BetType, Long> getBetRecord() {
        return betRecord;
    }

    public void setBetRecord(Map<Game.BetType, Long> betRecord) {
        this.betRecord = betRecord;
    }

    public List<Game.BetType> getGameResult() {
        return gameResult;
    }

    public void setGameResult(List<Game.BetType> gameResult) {
        this.gameResult = gameResult;
    }

    public long getUserCoin() {
        return userCoin;
    }

    public void setUserCoin(long userCoin) {
        this.userCoin = userCoin;
    }

    public boolean isBankerGame() {
        return isBankerGame;
    }

    public void setBankerGame(boolean bankerGame) {
        isBankerGame = bankerGame;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getRoomDisplayId() {
        return roomDisplayId;
    }

    public void setRoomDisplayId(String roomDisplayId) {
        this.roomDisplayId = roomDisplayId;
    }

    @Override
    public String toString() {
        return "DailyBetInfo{" +
                "id='" + id + '\'' +
                ", liveUserId='" + liveUserId + '\'' +
                ", betUserId='" + betUserId + '\'' +
                ", time=" + time +
                ", betAmount=" + betAmount +
                ", win=" + win +
                ", roomId='" + roomId + '\'' +
                ", gameType=" + gameType +
                ", betRecord=" + betRecord +
                ", gameResult=" + gameResult +
                ", userCoin=" + userCoin +
                ", isBankerGame=" + isBankerGame +
                '}';
    }
}
