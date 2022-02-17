package com.donglaistd.jinli.config;


import com.donglaistd.jinli.Constant;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "landlords")
public class LandlordGameConfig {
    private int gamePlayerNum;
    private int readyCountDownTime;
    private int grabCountDownTime;
    private int chooseRateCountDownTime;
    private int playCardCountDownTime;
    private int gameMaxPlayTime;
    private int robotWaitingTime;
    private int endWaitTime;
    private int messageDelayTime;
    private int springDelayTime;
    private int grabRate;
    private int chooseRate;
    private int springRate;
    private int bombRate;
    private int baseRate;
    private int gameMinCoin;

    public int getGamePlayerNum() {
        return gamePlayerNum;
    }

    public void setGamePlayerNum(int gamePlayerNum) {
        this.gamePlayerNum = gamePlayerNum;
    }

    public int getReadyCountDownTime() {
        return readyCountDownTime;
    }

    public void setReadyCountDownTime(int readyCountDownTime) {
        this.readyCountDownTime = readyCountDownTime;
    }

    public int getGrabCountDownTime() {
        return grabCountDownTime;
    }

    public void setGrabCountDownTime(int grabCountDownTime) {
        this.grabCountDownTime = grabCountDownTime;
    }

    public int getChooseRateCountDownTime() {
        return chooseRateCountDownTime;
    }

    public void setChooseRateCountDownTime(int chooseRateCountDownTime) {
        this.chooseRateCountDownTime = chooseRateCountDownTime;
    }

    public int getPlayCardCountDownTime() {
        return playCardCountDownTime;
    }

    public void setPlayCardCountDownTime(int playCardCountDownTime) {
        this.playCardCountDownTime = playCardCountDownTime;
    }

    public int getGameMaxPlayTime() {
        return gameMaxPlayTime;
    }

    public void setGameMaxPlayTime(int gameMaxPlayTime) {
        this.gameMaxPlayTime = gameMaxPlayTime;
    }

    public int getRobotWaitingTime() {
        return robotWaitingTime;
    }

    public void setRobotWaitingTime(int robotWaitingTime) {
        this.robotWaitingTime = robotWaitingTime;
    }

    public int getEndWaitTime() {
        return endWaitTime;
    }

    public void setEndWaitTime(int endWaitTime) {
        this.endWaitTime = endWaitTime;
    }

    public int getMessageDelayTime() {
        return messageDelayTime;
    }

    public void setMessageDelayTime(int messageDelayTime) {
        this.messageDelayTime = messageDelayTime;
    }

    public int getSpringDelayTime() {
        return springDelayTime;
    }

    public void setSpringDelayTime(int springDelayTime) {
        this.springDelayTime = springDelayTime;
    }

    public int getGrabRate() {
        return grabRate;
    }

    public void setGrabRate(int grabRate) {
        this.grabRate = grabRate;
    }

    public int getChooseRate() {
        return chooseRate;
    }

    public void setChooseRate(int chooseRate) {
        this.chooseRate = chooseRate;
    }

    public int getSpringRate() {
        return springRate;
    }

    public void setSpringRate(int springRate) {
        this.springRate = springRate;
    }

    public int getBombRate() {
        return bombRate;
    }

    public void setBombRate(int bombRate) {
        this.bombRate = bombRate;
    }

    public int getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(int baseRate) {
        this.baseRate = baseRate;
    }

    public int getGameMinCoin() {
        return gameMinCoin;
    }

    public void setGameMinCoin(int gameMinCoin) {
        this.gameMinCoin = gameMinCoin;
    }

    public Integer getRate(Constant.LandlordsRateType rateType){
        switch (rateType){
            case SPRING_RATE:
                return springRate;
            case CHOOSE_RATE:
                return chooseRate;
            case LANDLORDS_RATE:
                return grabRate;
            case BOMB_RATE:
                return bombRate;
        }
        return 1;
    }
}
