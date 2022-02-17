package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Constant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LiveGameInfo implements Serializable {
    private Constant.GameType gameType;
    private boolean isBankerGame;
    private String playingGameId;
    private List<PlatformRoomParam> platformRoomParams = new ArrayList<>();

    public static LiveGameInfo newInstance(Constant.GameType playingGameType, boolean isBankerGame,String playingGameId){
        return new LiveGameInfo(playingGameType, isBankerGame, playingGameId);
    }
    public LiveGameInfo(Constant.GameType gameType, boolean isBankerGame,String playingGameId) {
        this.gameType = gameType;
        this.isBankerGame = isBankerGame;
        this.playingGameId = playingGameId;
    }

    public LiveGameInfo() {
    }

    public Constant.GameType getGameType() {
        return gameType;
    }

    public void setGameType(Constant.GameType gameType) {
        this.gameType = gameType;
    }

    public boolean isBankerGame() {
        return isBankerGame;
    }

    public void setBankerGame(boolean bankerGame) {
        isBankerGame = bankerGame;
    }

    public String getPlayingGameId() {
        return playingGameId;
    }

    public void setPlayingGameId(String playingGameId) {
        this.playingGameId = playingGameId;
    }

    public List<PlatformRoomParam> getPlatformRoomParams() {
        return platformRoomParams;
    }

    public void setPlatformRoomParams(List<PlatformRoomParam> platformRoomParams) {
        this.platformRoomParams = platformRoomParams;
    }

    public static class PlatformRoomParam implements Serializable{
        public Constant.PlatformType platform;
        private String otherPlatformGameCode;
        private String otherPlatformRoomCode;

        public PlatformRoomParam(Constant.PlatformType platform, String otherPlatformGameCode, String otherPlatformRoomCode) {
            this.platform = platform;
            this.otherPlatformGameCode = otherPlatformGameCode;
            this.otherPlatformRoomCode = otherPlatformRoomCode;
        }
        public Constant.PlatformType getPlatform() {
            return platform;
        }

        public void setPlatform(Constant.PlatformType platform) {
            this.platform = platform;
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
    }
}
