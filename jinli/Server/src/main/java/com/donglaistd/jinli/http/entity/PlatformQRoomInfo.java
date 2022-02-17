package com.donglaistd.jinli.http.entity;

public class PlatformQRoomInfo {
    private String liveUserName;
    private String GameCode;
    private String RoomCode;
    private String roomUrl;
    private String liveUserAvatar;

    public PlatformQRoomInfo(String liveUserName, String gameCode, String roomCode, String roomUrl, String liveUserAvatar) {
        this.liveUserName = liveUserName;
        GameCode = gameCode;
        RoomCode = roomCode;
        this.roomUrl = roomUrl;
        this.liveUserAvatar = liveUserAvatar;
    }

    public String getLiveUserName() {
        return liveUserName;
    }

    public void setLiveUserName(String liveUserName) {
        this.liveUserName = liveUserName;
    }

    public String getGameCode() {
        return GameCode;
    }

    public void setGameCode(String gameCode) {
        GameCode = gameCode;
    }

    public String getRoomCode() {
        return RoomCode;
    }

    public void setRoomCode(String roomCode) {
        RoomCode = roomCode;
    }

    public String getRoomUrl() {
        return roomUrl;
    }

    public void setRoomUrl(String roomUrl) {
        this.roomUrl = roomUrl;
    }

    public String getLiveUserAvatar() {
        return liveUserAvatar;
    }

    public void setLiveUserAvatar(String liveUserAvatar) {
        this.liveUserAvatar = liveUserAvatar;
    }

    @Override
    public String toString() {
        return "PlatformQRoomInfo{" +
                "liveUserName='" + liveUserName + '\'' +
                ", GameCode='" + GameCode + '\'' +
                ", RoomCode='" + RoomCode + '\'' +
                ", roomUrl='" + roomUrl + '\'' +
                ", liveUserAvatar='" + liveUserAvatar + '\'' +
                '}';
    }
}
