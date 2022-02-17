package com.donglaistd.jinli.task.pocker;

import com.donglaistd.jinli.database.entity.game.landlord.Landlords;
import com.donglaistd.jinli.database.entity.game.landlord.PokerPlayer;

import java.util.List;
import java.util.logging.Logger;

public abstract class PockBaseTask {
    protected static final Logger logger = Logger.getLogger(PockBaseTask.class.getName());
    protected boolean isRun = true;

    protected PokerPlayer pokerPlayer;

    protected List<PokerPlayer> pokerPlayers;

    protected Landlords landlords;

    private final long startTime;

    private long countDownTime;

    public boolean isRun() {
        return isRun;
    }

    public void stopRun(){
        isRun = false;
    }

    public PokerPlayer getPokerPlayer() {
        return pokerPlayer;
    }

    public synchronized void setPokerPlayer(PokerPlayer pokerPlayer) {
        this.pokerPlayer = pokerPlayer;
    }

    public Landlords getLandlords() {
        return landlords;
    }

    public  void setLandlords(Landlords landlords) {
        this.landlords = landlords;
    }

    public long getCountDownTime() {
        return countDownTime;
    }

    public PockBaseTask(PokerPlayer pokerPlayer, Landlords landlords) {
        this.startTime = System.currentTimeMillis();
        this.pokerPlayer = pokerPlayer;
        this.landlords = landlords;
    }

    public PockBaseTask(Landlords landlords, long countDownTime) {
        this.startTime = System.currentTimeMillis();
        this.landlords = landlords;
        this.countDownTime = countDownTime;
    }

    public PockBaseTask(PokerPlayer pokerPlayer, Landlords landlords, long countDownTime) {
        this.startTime = System.currentTimeMillis();
        this.pokerPlayer = pokerPlayer;
        this.landlords = landlords;
        this.countDownTime = countDownTime;
    }

    public PockBaseTask(List<PokerPlayer> pokerPlayers, Landlords landlords, long countDownTime) {
        this.startTime = System.currentTimeMillis();
        this.pokerPlayers = pokerPlayers;
        this.landlords = landlords;
        this.countDownTime = countDownTime;
    }

    public PockBaseTask() {
        this.startTime = System.currentTimeMillis();
    }

    public abstract void runTask();

    public long getStartTime() {
        return startTime;
    }

    public long  getLeftTime() {
        return countDownTime - (System.currentTimeMillis() - startTime);
    }

    public List<PokerPlayer> getPokerPlayers() {
        return pokerPlayers;
    }

    public void setPokerPlayers(List<PokerPlayer> pokerPlayers) {
        this.pokerPlayers = pokerPlayers;
    }


}
