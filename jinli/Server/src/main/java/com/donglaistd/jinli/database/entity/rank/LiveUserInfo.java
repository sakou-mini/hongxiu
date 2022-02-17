package com.donglaistd.jinli.database.entity.rank;

import org.springframework.data.annotation.Id;

public class LiveUserInfo {
    @Id
    private String id;
    private long hotValue;

    public LiveUserInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getHotValue() {
        return hotValue;
    }

    public void setHotValue(int hotValue) {
        this.hotValue = hotValue;
    }

    public LiveUserInfo(String id, long hotValue) {
        this.id = id;
        this.hotValue = hotValue;
    }

    @Override
    public String toString() {
        return "LiveUserVo{" +
                "id=" + id +
                ", hotValue=" + hotValue +
                '}';
    }
}
