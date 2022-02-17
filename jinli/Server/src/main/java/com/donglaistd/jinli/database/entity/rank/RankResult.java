package com.donglaistd.jinli.database.entity.rank;

public class RankResult {
    private String userId;
    private long rank;
    private long score;

    public RankResult(String userId, long rank, long score) {
        this.userId = userId;
        this.rank = rank;
        this.score = score;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getRank() {
        return rank;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
