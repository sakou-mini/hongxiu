package com.donglaistd.jinli.exception;

import com.donglaistd.jinli.Constant;

public class JinliException extends RuntimeException {
    private static final long serialVersionUID = -7350428751518826835L;
    private final Constant.ResultCode resultCode;

    public JinliException(Constant.ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public Constant.ResultCode getResultCode() {
        return resultCode;
    }
}
