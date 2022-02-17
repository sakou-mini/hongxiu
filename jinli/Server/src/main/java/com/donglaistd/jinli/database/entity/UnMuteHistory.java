package com.donglaistd.jinli.database.entity;

import java.util.Objects;

public class UnMuteHistory {
    private String userId;
    private MuteProperty muteProperty;

    public UnMuteHistory(String userId, MuteProperty muteProperty) {
        this.userId = userId;
        this.muteProperty = muteProperty;
    }

    public static UnMuteHistory newInstance(String userId, MuteProperty muteProperty){
        return new UnMuteHistory(userId, muteProperty);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public MuteProperty getMuteProperty() {
        return muteProperty;
    }

    public void setMuteProperty(MuteProperty muteProperty) {
        this.muteProperty = muteProperty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnMuteHistory that = (UnMuteHistory) o;
        return userId.equals(that.userId) &&
                muteProperty.equals(that.muteProperty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, muteProperty);
    }
}
