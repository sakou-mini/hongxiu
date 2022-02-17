package com.donglaistd.jinli.http.entity.live;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LiveInfo {
    private int result;
    private String msg;
    private String liveUrl;
    private List<String> liveUrlList;

    public LiveInfo(int result, String msg) {
        this.result = result;
        this.msg = msg;
    }

    public LiveInfo(int result, String msg, String liveUrl) {
        this.result = result;
        this.msg = msg;
        this.liveUrl = liveUrl;
    }

    public LiveInfo(int result, String msg, List<String> liveUrlList) {
        this.result = result;
        this.msg = msg;
        this.liveUrlList = liveUrlList;
    }

    public LiveInfo() {
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getLiveUrl() {
        return liveUrl;
    }

    public void setLiveUrl(String liveUrl) {
        this.liveUrl = liveUrl;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<String> getLiveUrlList() {
        return liveUrlList;
    }

    public void setLiveUrlList(List<String> liveUrlList) {
        this.liveUrlList = liveUrlList;
    }
}
