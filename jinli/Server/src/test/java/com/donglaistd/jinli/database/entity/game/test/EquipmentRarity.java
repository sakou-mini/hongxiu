package com.donglaistd.jinli.database.entity.game.test;

import java.io.Serializable;

public class EquipmentRarity implements Serializable {
    private int rarityType; //稀有度
    private int start;		//星级

    public EquipmentRarity(int rarityType, int start) {
        this.rarityType = rarityType;
        this.start = start;
    }

    public int getRarityType() {
        return rarityType;
    }

    public void setRarityType(int rarityType) {
        this.rarityType = rarityType;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }
}
