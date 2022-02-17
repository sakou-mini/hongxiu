package com.donglaistd.jinli.http.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DomainResult {
    private int result;
    private String msg;
    private List<String> apiUrl;

    public DomainResult(int result, String msg) {
        this.result = result;
        this.msg = msg;
    }

    public DomainResult(int result, String msg, List<String> apiUrl) {
        this.result = result;
        this.msg = msg;
        this.apiUrl = apiUrl;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<String> getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(List<String> apiUrl) {
        this.apiUrl = apiUrl;
    }
}
