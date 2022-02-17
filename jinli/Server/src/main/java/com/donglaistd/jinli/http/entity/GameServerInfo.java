package com.donglaistd.jinli.http.entity;

import com.donglaistd.jinli.database.entity.system.GameServerConfig;

import java.io.Serializable;

public class GameServerInfo implements Serializable {

    private GameServerConfig.ServerStatue statue;

    private long onlineCount;

    private long liveCount;

    private long liveGameCount;

    private long raceCount;

    public GameServerInfo(GameServerConfig.ServerStatue statue, long onlineCount, long liveCount, long liveGameCount, long raceCount) {
        this.statue = statue;
        this.onlineCount = onlineCount;
        this.liveCount = liveCount;
        this.liveGameCount = liveGameCount;
        this.raceCount = raceCount;
    }

    public static GameServerInfo newInstance(GameServerConfig.ServerStatue statue, long onlineCount, long liveCount, long liveGameCount, long raceCount){
        return new GameServerInfo(statue,onlineCount,liveCount,liveGameCount,raceCount);
    }

    public GameServerConfig.ServerStatue getStatue() {
        return statue;
    }

    public void setStatue(GameServerConfig.ServerStatue statue) {
        this.statue = statue;
    }

    public long getOnlineCount() {
        return onlineCount;
    }

    public void setOnlineCount(long onlineCount) {
        this.onlineCount = onlineCount;
    }

    public long getLiveCount() {
        return liveCount;
    }

    public void setLiveCount(long liveCount) {
        this.liveCount = liveCount;
    }

    public long getLiveGameCount() {
        return liveGameCount;
    }

    public void setLiveGameCount(long liveGameCount) {
        this.liveGameCount = liveGameCount;
    }

    public long getRaceCount() {
        return raceCount;
    }

    public void setRaceCount(long raceCount) {
        this.raceCount = raceCount;
    }

}
