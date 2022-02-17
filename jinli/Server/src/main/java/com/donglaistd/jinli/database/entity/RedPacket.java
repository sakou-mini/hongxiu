package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.util.RandomUtil;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.donglaistd.jinli.Constant.RedPacketConfig.ROOM_ALL;

public class RedPacket {
    private ObjectId id = ObjectId.get();
    protected String userId;
    private Integer amountCoin;
    private long createTime;
    private long openTime = -1;
    private int redPacketNum;
    private AtomicInteger leftRedPacketNum = new AtomicInteger();
    private AtomicInteger leftAmountCoin = new AtomicInteger();
    private Constant.RedPacketConfig config = ROOM_ALL;
    private Set<String> awardRecords = new HashSet<>();
    @Transient
    private List<Integer> randomCoinList;
    @Transient
    private long expireTime;      //the destory overTime
    @Transient
    private long countDownTime; //the redpacket open CountDownTime
    @Transient
    private int minCoinRange;

    public RedPacket() {
    }

    private RedPacket(String userId, int amountCoin, int redPacketNum, int minCoinRange) {
        this.amountCoin = amountCoin;
        this.redPacketNum = redPacketNum;
        this.leftRedPacketNum.set(redPacketNum);
        this.leftAmountCoin.set(amountCoin);
        this.createTime = System.currentTimeMillis();
        this.userId = userId;
        this.minCoinRange = minCoinRange;
        this.randomCoinList = RandomUtil.getRandomCoinList(amountCoin, redPacketNum, minCoinRange, null);
    }

    public static RedPacket getInstance(String userId, int amount, int redPacketNum, int minCoinRange) {
        return new RedPacket(userId, amount, redPacketNum, minCoinRange);
    }

    public String getId() {
        return id.toString();
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getRedPacketNum() {
        return redPacketNum;
    }

    public void setRedPacketNum(int redPacketNum) {
        this.redPacketNum = redPacketNum;
    }

    public AtomicInteger getLeftRedPacketNum() {
        return leftRedPacketNum;
    }

    public boolean containUser(String userId) {
        return awardRecords.contains(userId);
    }

    public Integer getLeftAmountCoin() {
        return leftAmountCoin.get();
    }

    public Constant.RedPacketConfig getConfig() {
        return config;
    }

    public void setConfig(Constant.RedPacketConfig config) {
        this.config = config;
    }

    public Integer getAmountCoin() {
        return amountCoin;
    }

    public long getOpenTime() {
        return openTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setOpenTime(long openTime) {
        this.openTime = openTime;
    }

    public void startCountDown() {
        this.openTime = Calendar.getInstance().getTimeInMillis() + this.countDownTime;
    }

    public boolean isOver() {
        return this.leftAmountCoin.get() <= 0 || this.leftRedPacketNum.get() <= 0;
    }

    public Integer getRedPacketCoin() {
        int leftCoin = leftAmountCoin.get();
        if (this.leftRedPacketNum.get() <= 0) {
            return -1;
        }
        Integer coin = this.randomCoinList.get(this.leftRedPacketNum.decrementAndGet());
        leftAmountCoin.compareAndSet(leftCoin, leftCoin - coin);
        return coin;
    }

    public void addRecord(String userId) {
        this.awardRecords.add(userId);
    }

    public long getExpireTime() {
        return expireTime;
    }

    public long getEndTime() {
        return this.openTime + this.expireTime;
    }

    public long getCountDownTime() {
        return countDownTime;
    }

    public void setCountDownTime(long countDownTime) {
        this.countDownTime = countDownTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public int getMinCoinRange() {
        return minCoinRange;
    }

    public Jinli.RedPacketInfo toProto(String userId) {
        return Jinli.RedPacketInfo.newBuilder().setId(getId()).setCoinAmount(amountCoin)
                .setTotalNum(redPacketNum).setCreateTime(createTime).setOpenTime(openTime).setLeftNum(leftRedPacketNum.get())
                .setOpenFlag(containUser(userId)).setCountDownTime(this.countDownTime).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RedPacket redPacket = (RedPacket) o;
        return id.equals(redPacket.id) &&
                userId.equals(redPacket.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId);
    }

    public List<Integer> getRandomCoinList() {
        return randomCoinList;
    }

    @Override
    public String toString() {
        return "RedPacket{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", amountCoin=" + amountCoin +
                ", createTime=" + createTime +
                ", openTime=" + openTime +
                ", redPacketNum=" + redPacketNum +
                ", leftRedPacketNum=" + leftRedPacketNum +
                ", leftAmountCoin=" + leftAmountCoin +
                ", config=" + config +
                ", awardRecords=" + awardRecords +
                ", randomCoinList=" + randomCoinList +
                ", expireTime=" + expireTime +
                ", countDownTime=" + countDownTime +
                '}';
    }
}
