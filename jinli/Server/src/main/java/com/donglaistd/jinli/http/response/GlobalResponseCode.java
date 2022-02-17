package com.donglaistd.jinli.http.response;

public enum GlobalResponseCode implements IErrorCode {
    SUCCESS(200, "操作成功！"),

    LIVE_USER_NOT_FOUNT(205, "主播不存在"),
    REQUEST_PARAM_ERROR(300, "请求参数错误");

    private int code;
    private String message;

    GlobalResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
