package com.donglaistd.jinli.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "texas")
public class TexasConfig {
    private int maxChips;
    private int minChips;
    private int maxPlayers;
    private int minPlayers;
    private int optTimeout;
    private int communityCardSize;
    private int restBetweenGame;
    private int readyCountDownTime;
    private int increaseBetTime;
    private int baseReward;
    private int delayTime;
    public void setBaseReward(int baseReward) {
        this.baseReward = baseReward;
    }

    public int getIncreaseBetTime() {
        return increaseBetTime;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public void setIncreaseBetTime(int increaseBetTime) {
        this.increaseBetTime = increaseBetTime;
    }

    public int getBaseReward() {
        return baseReward;
    }

    public int getMaxChips() {
        return maxChips;
    }

    public void setMaxChips(int maxChips) {
        this.maxChips = maxChips;
    }

    public int getMinChips() {
        return minChips;
    }

    public void setMinChips(int minChips) {
        this.minChips = minChips;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
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

    public int getCommunityCardSize() {
        return communityCardSize;
    }

    public void setCommunityCardSize(int communityCardSize) {
        this.communityCardSize = communityCardSize;
    }

    public int getRestBetweenGame() {
        return restBetweenGame;
    }

    public void setRestBetweenGame(int restBetweenGame) {
        this.restBetweenGame = restBetweenGame;
    }

    public int getReadyCountDownTime() {
        return readyCountDownTime;
    }

    public void setReadyCountDownTime(int readyCountDownTime) {
        this.readyCountDownTime = readyCountDownTime;
    }
}
