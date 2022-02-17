package com.donglaistd.jinli.config;

import com.donglaistd.jinli.Jinli;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.donglaistd.jinli.constant.GameConstant.FIRST_RANK;
import static com.donglaistd.jinli.constant.GameConstant.SECOND_RANK;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GoldenFlowerRaceConfig {
    @Value("${goldenFlower.race.raceFee}")
    private int raceFee;
    @Value("${goldenFlower.race.startingChips}")
    private int  startingChips;
    @Value("${goldenFlower.race.firstCoin.coin}")
    private double firstCoin;
    @Value("${goldenFlower.race.second.coin}")
    private double secondCoin;
    @Value("${goldenFlower.race.minPlayers}")
    private int minPlayers;
    @Value("${goldenFlower.race.maxPlayers}")
    private int maxPlayers;
    @Value("${goldenFlower.race.gameCount}")
    private int gameCount;
    @Value("${goldenFlower.race.registrationTime}")
    private int registrationTime;
    @Value("${goldenFlower.race.baseReward}")
    private int baseReward;
    @Value("${goldenFlower.race.cost}")
    private int cost;
    private final Map<Integer, Double> rankCoinAward = new HashMap<>();
    private int raceLevel = 1;

    public GoldenFlowerRaceConfig(@Value("${goldenFlower.race.firstCoin.coin}") double firstCoin,
                                  @Value("${goldenFlower.race.second.coin}") double secondCoin ) {
        rankCoinAward.put(FIRST_RANK, firstCoin);
        rankCoinAward.put(SECOND_RANK, secondCoin);
    }

    public int getRegistrationTime() {
        return registrationTime;
    }

    public int getBaseReward() {
        return baseReward;
    }

    public void setBaseReward(int baseReward) {
        this.baseReward = baseReward;
    }

    public void setRegistrationTime(int registrationTime) {
        this.registrationTime = registrationTime;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getRaceFee() {
        return raceFee;
    }

    public int getGameCount() {
        return gameCount;
    }

    public void setGameCount(int gameCount) {
        this.gameCount = gameCount;
    }

    public void setRaceFee(int raceFee) {
        this.raceFee = raceFee;
    }

    public int getStartingChips() {
        return startingChips;
    }

    public void setStartingChips(int startingChips) {
        this.startingChips = startingChips;
    }

    public double getFirstCoin() {
        return firstCoin;
    }

    public void setFirstCoin(double firstCoin) {
        this.firstCoin = firstCoin;
    }

    public double getSecondCoin() {
        return secondCoin;
    }

    public void setSecondCoin(double secondCoin) {
        this.secondCoin = secondCoin;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getRaceLevel() {
        return raceLevel;
    }

    public Jinli.GoldenFlowerRaceConfig toProto(int rewardAmount, int startTime) {
        return Jinli.GoldenFlowerRaceConfig.newBuilder()
                .setMinPlayers(minPlayers)
                .setMaxPlayers(maxPlayers)
                .setStartingChips(startingChips)
                .setCost(cost)
                .setRewardAmount(rewardAmount)
                .setStartTime(startTime)
                .setGameCount(gameCount).build();
    }

    public Map<Integer, Double> getRankCoinAward(){
        return rankCoinAward;
    }

    public void upgradeRaceLevel(int level) {
        if(level<=0) return;
        this.raceLevel = level;
        this.raceFee *= level;
    }

}
