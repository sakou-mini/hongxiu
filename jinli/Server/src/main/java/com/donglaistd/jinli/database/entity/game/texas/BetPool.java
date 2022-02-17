package com.donglaistd.jinli.database.entity.game.texas;

import java.util.ArrayList;
import java.util.List;

public class BetPool {
    // 分池总金额
    private long betSum;
    // 分池玩家列表
    private List<RacePokerPlayer> betPlayerList = new ArrayList<>();

    public long getBetSum() {
        return betSum;
    }

    public void setBetSum(long betSum) {
        this.betSum = betSum;
    }

    public List<RacePokerPlayer> getBetPlayerList() {
        return betPlayerList;
    }

    public void setBetPlayerList(List<RacePokerPlayer> betPlayerList) {
        this.betPlayerList = betPlayerList;
    }
}
