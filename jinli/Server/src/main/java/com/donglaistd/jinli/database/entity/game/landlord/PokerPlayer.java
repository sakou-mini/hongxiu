package com.donglaistd.jinli.database.entity.game.landlord;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.race.BasePokerPlayer;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.donglaistd.jinli.Constant.GrabLordStatue.*;

public class PokerPlayer extends BasePokerPlayer {

    private boolean openRobot = false;

    private int playCardCount = 0;

    private final Set<Integer> winRoundRecord = new HashSet<>();

    private int chooseRate = 1;

    private boolean isLandLord = false;

    private Constant.GrabLordStatue grabLordStatue = GrabLordStatue_NO;

    public String getUserId() {
        return user.getId();
    }

    public void openRobot() {
        this.openRobot = true;
    }

    public boolean isOpenRobot() {
        return openRobot;
    }

    public int getPlayCardCount() {
        return playCardCount;
    }

    public void passGrabLord() {
        grabLordStatue = GrabLordStatue_PASS;
    }

    public void grabLord() {
        grabLordStatue = GrabLordStatue_APPLY;
    }

    public boolean hasGrabLord() {
        return !grabLordStatue.equals(GrabLordStatue_NO);
    }

    public int getChooseRate() {
        return chooseRate;
    }

    public void setChooseRate(int chooseRate) {
        this.chooseRate = chooseRate;
    }

    public void becomeLandlord() {
        this.isLandLord = true;
    }

    public void addCoin(long score) {
        this.initCoin += score;
    }

    public void reset() {
        this.isLandLord = false;
        chooseRate = 1;
        playCardCount = 0;
        this.grabLordStatue = GrabLordStatue_NO;
    }

    public void addWinRoundRecord(int round) {
        this.winRoundRecord.add(round);
    }

    public Constant.GrabLordStatue getGrabLordStatue() {
        return grabLordStatue;
    }

    public void addPlayCount() {
        this.playCardCount++;
    }

    public Set<Integer> getWinRoundRecord() {
        return winRoundRecord;
    }

    public int sumWinRound() {
        return winRoundRecord.stream().mapToInt(Integer::intValue).sum();
    }

    public PokerPlayer(User user, int seatNum, long initCoin) {
        this.user = user;
        this.seatNum = seatNum;
        this.initCoin = initCoin;
    }

    public static PokerPlayer newInstance(User user, int seatNum, long initCoin) {
        return new PokerPlayer(user, seatNum, initCoin);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PokerPlayer that = (PokerPlayer) o;
        return Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

    public Jinli.PokerPlayer toProto() {
        return Jinli.PokerPlayer.newBuilder().setUserInfo(user.toSummaryProto()).setOpenRobot(openRobot)
                .setPosition(seatNum).setScore(initCoin).setRank(rank).setPlayCardCount(playCardCount).setIsLandlords(isLandLord).build();
    }
}
