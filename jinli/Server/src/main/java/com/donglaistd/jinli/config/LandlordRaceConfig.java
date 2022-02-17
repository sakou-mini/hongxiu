package com.donglaistd.jinli.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LandlordRaceConfig {
    @Value("${landlords.race.fees}")
    private int raceFee;
    @Value("${landlords.race.joinPeopleNum}")
    private int joinPeopleNum;
    @Value("${landlords.race.baseCoin}")
    private int baseCoin;
    @Value("${landlords.race.match.waitTime}")
    private int matchWaitTime;
    @Value("${landlords.race.firstRace.round}")
    private int firstRaceRound;
    @Value("${landlords.race.secondRace.round}")
    private int secondRaceRound;
    @Value("${landlords.race.first.coin}")
    private int firstCoin;
    @Value("${landlords.race.second.coin}")
    private int secondCoin;
    @Value("${landlords.race.third.coin}")
    private int threadCoin;
    @Value("${landlords.gamePlayerNum}")
    private int gamePeopleNum;
    @Value("${landlords.race.weekOut.rank}")
    private int weekOutRank;
    @Value("${landlords.race.robotNum}")
    private int robotNum;
    @Value("${landlords.race.begin.delay.time}")
    private long raceBeginDelayTime;
    private int raceLevel;

    public int getRaceFee() {
        return raceFee;
    }

    public int getJoinPeopleNum() {
        return joinPeopleNum;
    }

    public int getBaseCoin() {
        return baseCoin;
    }

    public int getMatchWaitTime() {
        return matchWaitTime;
    }

    public int getFirstRaceRound() {
        return firstRaceRound;
    }

    public int getSecondRaceRound() {
        return secondRaceRound;
    }

    public int getGamePeopleNum() {
        return gamePeopleNum;
    }

    public int getWeekOutRank() {
        return weekOutRank;
    }

    public void setFirstRaceRound(int firstRaceRound) {
        this.firstRaceRound = firstRaceRound;
    }

    public void setSecondRaceRound(int secondRaceRound) {
        this.secondRaceRound = secondRaceRound;
    }

    public int getRobotNum() {
        return robotNum;
    }

    public long getRaceBeginDelayTime() {
        return raceBeginDelayTime;
    }

    public int getRaceLevel() {
        return raceLevel;
    }

    public int getRankAward(int rank){
        switch (rank){
            case 1:
                return firstCoin;
            case 2:
                return secondCoin;
            case 3:
                return threadCoin;
        }
        return 0;
    }

    public void upgradeRaceLevel(int level) {
        if(level<=0) return;
        this.raceFee *= level;
        this.firstCoin *= level;
        this.secondCoin *= level;
        this.threadCoin *= level;
        this.raceLevel = level;
    }

}
