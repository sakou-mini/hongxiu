package com.donglaistd.jinli.http.entity;

import com.donglaistd.jinli.database.entity.GiftOrder;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.plant.PlatformRechargeRecord;

public class RechargeLog {
    public UserSummary userSummary;
    public long rechargeCoin;
    public long rechargeTime;
    public long leftCoin;
    public long rechargeTotalAmount;
    public int platformTag;

    public RechargeLog(UserSummary userSummary, long rechargeCoin, long rechargeTime, long leftCoin, long rechargeTotalAmount,int platformTag) {
        this.userSummary = userSummary;
        this.rechargeCoin = rechargeCoin;
        this.rechargeTime = rechargeTime;
        this.leftCoin = leftCoin;
        this.rechargeTotalAmount = rechargeTotalAmount;
        this.platformTag = platformTag;
    }

    public static RechargeLog newInstance(User user, PlatformRechargeRecord rechargeRecord,int platformTag) {
        return new RechargeLog(UserSummary.newInstance(user), rechargeRecord.getRechargeGameCoin(), rechargeRecord.getRechargeTime(),
                rechargeRecord.getLeftGameCoin(), rechargeRecord.getTotalRechargeAmount(),platformTag);
    }

    public static RechargeLog newInstance(User user, GiftOrder giftOrder,int platformTag) {
        return new RechargeLog(UserSummary.newInstance(user), giftOrder.getTotalPrice(), giftOrder.getCreateTime(),
                0, giftOrder.getSendAmount(),platformTag);
    }
}
