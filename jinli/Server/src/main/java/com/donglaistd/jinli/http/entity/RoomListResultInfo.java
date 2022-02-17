package com.donglaistd.jinli.http.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomListResultInfo {
    private int result;
    private String msg;
    private List<PlatformQRoomInfo> roomList;
    private List<String> lineList;
    private String loadUrl;
    public RoomListResultInfo(int result, String msg) {
        this.result = result;
        this.msg = msg;
    }

    public RoomListResultInfo(int result, String msg, List<PlatformQRoomInfo> roomList,List<String> lineList) {
        this.result = result;
        this.msg = msg;
        this.roomList = roomList;
        this.lineList = lineList;
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

    public List<PlatformQRoomInfo> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<PlatformQRoomInfo> roomList) {
        this.roomList = roomList;
    }

    public List<String> getLineList() {
        return lineList;
    }

    public void setLineList(List<String> lineList) {
        this.lineList = lineList;
    }

    public String getLoadUrl() {
        return loadUrl;
    }

    public void setLoadUrl(String loadUrl) {
        this.loadUrl = loadUrl;
    }
}
