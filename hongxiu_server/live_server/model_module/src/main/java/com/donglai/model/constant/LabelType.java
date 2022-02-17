package com.donglai.model.constant;

public enum LabelType {
    SYSTEM_LABEL(0), CUSTOM_LABEL(1);
    public int value;

    LabelType(int value) {
        this.value = value;
    }

    public static LabelType forNumber(int code) {
        switch (code) {
            case 0:
                return SYSTEM_LABEL;
            case 1:
                return CUSTOM_LABEL;
            default:
                return null;
        }
    }
}
