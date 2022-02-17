package com.donglaistd.jinli.database.entity.race;

public class UserRace {
    private String userId;
    private String gameId;
    private String raceId;

    public UserRace(String userId, String raceId) {
        this.userId = userId;
        this.raceId = raceId;
    }

    public UserRace(String userId, String gameId, String raceId) {
        this.userId = userId;
        this.gameId = gameId;
        this.raceId = raceId;
    }

    public static UserRace newInstance(String userId, String gameId, String raceId){
        return new UserRace(userId, gameId, raceId);
    }

    public static UserRace newInstance(String userId,String raceId){
        return new UserRace(userId, raceId);
    }

    public String getUserId() {
        return userId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getRaceId() {
        return raceId;
    }

    public void setRaceId(String raceId) {
        this.raceId = raceId;
    }

    @Override
    public String toString() {
        return "UserRace{" +
                "userId='" + userId + '\'' +
                ", raceId='" + raceId + '\'' +
                '}';
    }
}
