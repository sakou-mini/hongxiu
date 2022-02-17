package com.donglaistd.jinli.http.entity;

public class RtmpOriginInfo {
    private String ip;
    private String type;
    private String cdnip;
    private String host;
    private String app; //publish method
    private String userId;
    private String token;
    private String liveUrl;
    private String roomId;
    private String channel;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCdnip() {
        return cdnip;
    }

    public void setCdnip(String cdnip) {
        this.cdnip = cdnip;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLiveUrl() {
        return liveUrl;
    }

    public void setLiveUrl(String liveUrl) {
        this.liveUrl = liveUrl;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "RtmpOriginInfo{" +
                "ip='" + ip + '\'' +
                ", type='" + type + '\'' +
                ", cdnip='" + cdnip + '\'' +
                ", host='" + host + '\'' +
                ", app='" + app + '\'' +
                ", userId='" + userId + '\'' +
                ", token='" + token + '\'' +
                ", liveUrl='" + liveUrl + '\'' +
                ", roomId='" + roomId + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
}
