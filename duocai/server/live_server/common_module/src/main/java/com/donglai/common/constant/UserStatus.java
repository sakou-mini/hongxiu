package com.donglai.common.constant;

import lombok.Getter;

@Getter
public enum UserStatus {
    NORMAL(0),BAN(1);

    private final int value;

    UserStatus(int i) {
        this.value = i;
    }
}
