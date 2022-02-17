package com.donglaistd.jinli.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QPlatformGameConfig {
    private String gameName;
    private String gameCode;
    private String roomName;
    private String roomCode;

    @JsonCreator
    public QPlatformGameConfig(@JsonProperty("gameName") String gameName, @JsonProperty("gameCode") String gameCode,
                               @JsonProperty("roomName") String roomName, @JsonProperty("roomCode")String roomCode) {
        this.gameName = gameName;
        this.gameCode = gameCode;
        this.roomName = roomName;
        this.roomCode = roomCode;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }
}
