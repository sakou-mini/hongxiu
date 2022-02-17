package com.donglaistd.jinli.database.entity.game.test;

import java.util.Map;

public class SuitFitting {
    private EquipmentRarity equipmentRarity;
    private long requiredNum;

    private SuitPromote promotionInfo; //套装提升

    public EquipmentRarity getEquipmentRarity() {
        return equipmentRarity;
    }

    public void setEquipmentRarity(EquipmentRarity equipmentRarity) {
        this.equipmentRarity = equipmentRarity;
    }

    public SuitPromote getPromotionInfo() {
        return promotionInfo;
    }

    public void setPromotionInfo(SuitPromote promotionInfo) {
        this.promotionInfo = promotionInfo;
    }

    public long getRequiredNum() {
        return requiredNum;
    }

    public void setRequiredNum(long requiredNum) {
        this.requiredNum = requiredNum;
    }

    public boolean isActiveSuit(Map<EquipmentRarity,Long> playerSuit){
        var num = playerSuit.get(equipmentRarity);
        return num >= requiredNum;
    }
}
