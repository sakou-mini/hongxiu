package com.donglaistd.jinli.database.entity.race;

import com.donglaistd.jinli.database.entity.User;

public abstract class BasePokerPlayer {

    protected User user;

    protected int seatNum = -1;

    protected long initCoin = 0;

    protected int rank;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(int seatNum) {
        this.seatNum = seatNum;
    }

    public long getInitCoin() {
        return initCoin;
    }

    public void setInitCoin(long initCoin) {
        this.initCoin = initCoin;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
