package com.donglaistd.jinli.database.entity.game.test;

public class Equipment {
    private int equipmentConfigId;
    private Long attack;
    private Long life;
    private Long defense;
    private Long speed;

    private EquipmentSuitConfig equipmentSuitConfig;

    public int getEquipmentConfigId() {
        return equipmentConfigId;
    }

    public void setEquipmentConfigId(int equipmentConfigId) {
        this.equipmentConfigId = equipmentConfigId;
    }

    public Long getAttack() {
        return attack;
    }

    public void setAttack(Long attack) {
        this.attack = attack;
    }

    public Long getLife() {
        return life;
    }

    public void setLife(Long life) {
        this.life = life;
    }

    public Long getDefense() {
        return defense;
    }

    public void setDefense(Long defense) {
        this.defense = defense;
    }

    public Long getSpeed() {
        return speed;
    }

    public void setSpeed(Long speed) {
        this.speed = speed;
    }

    public EquipmentSuitConfig getEquipmentSuitConfig() {
        return equipmentSuitConfig;
    }

    public void setEquipmentSuitConfig(EquipmentSuitConfig equipmentSuitConfig) {
        this.equipmentSuitConfig = equipmentSuitConfig;
    }
}
