package com.donglaistd.jinli.event;

import com.donglaistd.jinli.database.entity.User;

public class ModifyUserResourceEvent implements BaseEvent {
    private String userId;
    private long amount;
    private ModifyType modifyType;
    public enum ModifyType{
        gameCoin,
        goldBean,
        exp,
    }

    public ModifyUserResourceEvent(String userId, long amount, ModifyType modifyType) {
        this.userId = userId;
        this.amount = amount;
        this.modifyType = modifyType;
    }

    public ModifyUserResourceEvent(User user, long amount, ModifyType modifyType) {
        this.userId = user.getId();
        this.amount = amount;
        this.modifyType = modifyType;
    }

    public static ModifyUserResourceEvent newInstance(String userId, long amount, ModifyType modifyType) {
        return new ModifyUserResourceEvent(userId, amount, modifyType);
    }

    public String getUserId() {
        return userId;
    }

    public long getAmount() {
        return amount;
    }

    public ModifyType getModifyType() {
        return modifyType;
    }
}
