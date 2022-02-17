package com.donglaistd.jinli.http.message;

import com.donglaistd.jinli.Constant;

public class HttpMessageReply {
    private int messageId;
    private Constant.ResultCode resultCode;
    private Object reply;

    public HttpMessageReply(int messageId, Constant.ResultCode resultCode) {
        this.messageId = messageId;
        this.resultCode = resultCode;
    }

    public HttpMessageReply(int messageId, Constant.ResultCode resultCode, Object reply) {
        this.messageId = messageId;
        this.resultCode = resultCode;
        this.reply = reply;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public Constant.ResultCode getResultCode() {
        return resultCode;
    }

    public void setResultCode(Constant.ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public Object getReply() {
        return reply;
    }

    public void setReply(Object reply) {
        this.reply = reply;
    }
}
