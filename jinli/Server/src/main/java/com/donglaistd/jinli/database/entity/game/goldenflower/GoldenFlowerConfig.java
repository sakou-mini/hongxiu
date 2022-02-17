package com.donglaistd.jinli.database.entity.game.goldenflower;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "friedgoldenflower")
public class GoldenFlowerConfig {
    private int minPlayers;
    private int optTimeout;
    private int maxBright;
    private int maxDark;
    private int maxHands;
    private int restBetweenGame;
    private int readyCountDownTime;
    private int baseBet;
    private int delayTime;
    private int handPokerSize;
    private int baseRate;

    public int getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(int baseRate) {
        this.baseRate = baseRate;
    }

    public int getBaseBet() {
        return baseBet;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public int getHandPokerSize() {
        return handPokerSize;
    }

    public void setHandPokerSize(int handPokerSize) {
        this.handPokerSize = handPokerSize;
    }

    public void setBaseBet(int baseBet) {
        this.baseBet = baseBet;
    }

    public int getRestBetweenGame() {
        return restBetweenGame;
    }

    public int getReadyCountDownTime() {
        return readyCountDownTime;
    }

    public void setReadyCountDownTime(int readyCountDownTime) {
        this.readyCountDownTime = readyCountDownTime;
    }

    public void setRestBetweenGame(int restBetweenGame) {
        this.restBetweenGame = restBetweenGame;
    }


    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getOptTimeout() {
        return optTimeout;
    }

    public void setOptTimeout(int optTimeout) {
        this.optTimeout = optTimeout;
    }

    public int getMaxBright() {
        return maxBright;
    }

    public void setMaxBright(int maxBright) {
        this.maxBright = maxBright;
    }

    public int getMaxDark() {
        return maxDark;
    }

    public void setMaxDark(int maxDark) {
        this.maxDark = maxDark;
    }

    public int getMaxHands() {
        return maxHands;
    }

    public void setMaxHands(int maxHands) {
        this.maxHands = maxHands;
    }
}
