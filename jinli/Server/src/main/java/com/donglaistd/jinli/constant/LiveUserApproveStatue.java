package com.donglaistd.jinli.constant;

public enum  LiveUserApproveStatue {
    UNAPPROVE(1),PASS(2),NO_PASS(3);
    private final int code;
    LiveUserApproveStatue(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
