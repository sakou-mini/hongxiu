package com.donglaistd.jinli.http.entity;

public class RaceInfo {
    private long raceNum;
    private long raceGameNum;

    public long getRaceNum() {
        return raceNum;
    }

    public void setRaceNum(long raceNum) {
        this.raceNum = raceNum;
    }

    public long getRaceGameNum() {
        return raceGameNum;
    }

    public void setRaceGameNum(long raceGameNum) {
        this.raceGameNum = raceGameNum;
    }

    public RaceInfo(long raceNum, long raceGameNum) {
        this.raceNum = raceNum;
        this.raceGameNum = raceGameNum;
    }
}
