package com.donglaistd.jinli.database.entity.game.test;

import java.util.List;

public class EquipmentSuitConfig {

    private int suitModuleId;

    private String suitName;

    private List<SuitFitting> suitGroups;

    public int getSuitModuleId() {
        return suitModuleId;
    }

    public void setSuitModuleId(int suitModuleId) {
        this.suitModuleId = suitModuleId;
    }

    public String getSuitName() {
        return suitName;
    }

    public void setSuitName(String suitName) {
        this.suitName = suitName;
    }

    public List<SuitFitting> getSuitGroups() {
        return suitGroups;
    }

    public void setSuitGroups(List<SuitFitting> suitGroups) {
        this.suitGroups = suitGroups;
    }
}
