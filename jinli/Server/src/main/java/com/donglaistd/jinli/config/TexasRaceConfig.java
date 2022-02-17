package com.donglaistd.jinli.config;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.util.MetaUtil;

import java.util.*;
import java.util.stream.Collectors;

public class TexasRaceConfig {
    private int raceFee;
    private int  serviceCharge;
    private int minPlayers;
    private int maxPlayers;
    private int registrationTime;
    private int  startingChips;
    private String title;
    private int deadline;
    private Map<Integer, Double> rankCoinAward = new HashMap<>();
    private int raceLevel =1;

    public int getMinPlayers() {
        return minPlayers;
    }

    public Map<Integer, Double> getRankCoinAward() {
        return rankCoinAward;
    }

    public Double getCoinAward(int rank) {
        return rankCoinAward.get(rank);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    public void setRankCoinAward(Map<Integer, Double> rankCoinAward) {
        this.rankCoinAward = rankCoinAward;
    }

    public int getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(int serviceCharge) {
        this.serviceCharge = serviceCharge;
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

    public int getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(int registrationTime) {
        this.registrationTime = registrationTime;
    }

    public int getStartingChips() {
        return startingChips;
    }

    public void setStartingChips(int startingChips) {
        this.startingChips = startingChips;
    }


    public int getRaceFee() {
        return raceFee;
    }

    public void setRaceFee(int raceFee) {
        this.raceFee = raceFee;
    }

    public int getRaceLevel() {
        return raceLevel;
    }

    public Jinli.TexasRaceConfig toProto(int rewardAmount,int startTime) {
        Jinli.TexasRaceConfig.Builder builder = Jinli.TexasRaceConfig.newBuilder();
        builder.setMinPlayers(this.minPlayers)
                .setMaxPlayers(this.maxPlayers)
                .setStartingChips(this.startingChips)
                .setIncreaseBetTime(MetaUtil.getIncreaseBetDefineByLevel(1).getTime())
                .setDeadline(this.deadline)
                .setRewardAmount(rewardAmount)
                .setStartTime(startTime)
                .addAllReward(buildReward())
                .addAllTable(MetaUtil.buildStructureTable());
        return builder.build();
    }

    public List<Jinli.RewardMap> buildReward() {
        List<Jinli.RewardMap> list = new ArrayList<>();
        this.rankCoinAward.forEach((k,v)->{
            list.add(Jinli.RewardMap.newBuilder().setRank(k).setBonus(v).build());
        });
        return list.stream().sorted(Comparator.comparing(Jinli.RewardMap::getRank)).collect(Collectors.toList());
    }
    public void upgradeRaceLevel(int level){
        if(level <= 0) return;
        this.raceLevel = level;
        setRaceFee(getRaceFee()*level);
    }
}
