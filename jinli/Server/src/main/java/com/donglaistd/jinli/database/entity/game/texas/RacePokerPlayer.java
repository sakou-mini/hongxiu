package com.donglaistd.jinli.database.entity.game.texas;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.race.BasePokerPlayer;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.List;

public class RacePokerPlayer extends BasePokerPlayer implements Serializable {
    private static final long serialVersionUID = 1L;
    @Transient
    private User user;
    @Transient
    private int seatNum = -1;
    @Transient
    private long bringInChips;
    @Transient
    private Texas texas;
    @Transient
    private boolean isFold = true;
    @Transient
    private List<Card> handPokers;
    @Transient
    private int betTimes;
    @Transient
    private boolean isLook = false;

    public int getBetTimes() {
        return betTimes;
    }

    public void setBetTimes(int betTimes) {
        this.betTimes = betTimes;
    }

    public boolean isLook() {
        return isLook;
    }

    public void setLook(boolean look) {
        isLook = look;
    }

    public long getInitCoin() {
        return initCoin;
    }

    public void setInitCoin(Integer initCoin) {
        this.initCoin = initCoin;
    }

    public int getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(int seatNum) {
        this.seatNum = seatNum;
    }

    public long getBringInChips() {
        return bringInChips;
    }

    public void setBringInChips(long bringInChips) {
        this.bringInChips = bringInChips;
    }

    public void addBodyChips(int chips) {
        this.bringInChips += chips;
    }

    public Texas getTexas() {
        return texas;
    }

    public void setTexas(Texas texas) {
        this.texas = texas;
    }

    public boolean isFold() {
        return isFold;
    }

    public void setFold(boolean fold) {
        isFold = fold;
    }

    public List<Card> getHandPokers() {
        return handPokers;
    }

    public void setHandPokers(List<Card> handPokers) {
        this.handPokers = handPokers;
    }

    public User getUser() {
        return user;
    }

    public RacePokerPlayer(User user) {
        this.user = user;
    }

    public RacePokerPlayer() {
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Jinli.TexasPokerPlayer toProto() {
        Jinli.TexasPokerPlayer.Builder builder = Jinli.TexasPokerPlayer.newBuilder()
                .setIsFold(this.isFold)
                .setSeatNum(this.seatNum)
                .setChips((int) this.bringInChips)
                .setUser(user.toSummaryProto());
        return builder.build();
    }

    public String getUserId() {
        return user.getId();
    }

    public void reset() {
        setBetTimes(0);
        setFold(false);
        setLook(false);
        setHandPokers(null);
        setInitCoin((int) getBringInChips());
        setRank(0);
    }

    public Jinli.FlowerUser toFlowerUser() {
        Jinli.FlowerUser.Builder builder = Jinli.FlowerUser.newBuilder();
        builder.setUser(user.toSummaryProto())
                .setSeatNum(seatNum)
                .setIsFold(isFold)
                .setIsLook(isLook)
                .setChips((int) this.bringInChips);
        return builder.build();
    }
}
