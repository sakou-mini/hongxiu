package com.donglaistd.jinli.database.entity.statistic.record;

import java.util.concurrent.atomic.AtomicInteger;

public class UserRoomRecord {
    private long enterRoomTime;
    private final AtomicInteger giftCount = new AtomicInteger();
    private final AtomicInteger giftCoin  = new AtomicInteger();
    private final AtomicInteger connectedLiveCount  = new AtomicInteger();
    private final AtomicInteger bulletMessageCount = new AtomicInteger();
    private String roomId;
    private String userId;



    public UserRoomRecord() {
    }

    public UserRoomRecord(long enterRoomTime, String roomId,String userId) {
        this.enterRoomTime = enterRoomTime;
        this.roomId = roomId;
        this.userId = userId;
    }

    public void setEnterRoomTime(long enterRoomTime) {
        this.enterRoomTime = enterRoomTime;
    }

    public long getEnterRoomTime() {
        return enterRoomTime;
    }

    public int getGiftCount() {
        return giftCount.get();
    }

    public int getGiftCoin() {
        return giftCoin.get();
    }

    public int getConnectedLiveCount() {
        return connectedLiveCount.get();
    }

    public int getBulletMessageCount() {
        return bulletMessageCount.get();
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void addGiftCoin(long giftCoin){
        this.giftCount.incrementAndGet();
        this.giftCoin.addAndGet((int) giftCoin);
    }

    public void addConnectedLiveCount(){
        this.connectedLiveCount.incrementAndGet();
    }

    public void addBulletMessageCount(){
        this.bulletMessageCount.incrementAndGet();
    }
}
