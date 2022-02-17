package com.donglaistd.jinli.http.message;

public class HttpMessageRequest {
    private int messageId;
    private Object request;

    public HttpMessageRequest() {
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public Object getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        this.request = request;
    }
}
