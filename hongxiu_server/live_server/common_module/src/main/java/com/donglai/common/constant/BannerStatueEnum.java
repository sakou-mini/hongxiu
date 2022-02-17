package com.donglai.common.constant;
public enum BannerStatueEnum {
    /**
     * 状态  {} 0 待运行  1 运行中  2 已结束 3 已暂停}
     */
    NOT_START(0),RUNNING(1),OVER(2), PAUSE(3);
    private final int value;

    BannerStatueEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
