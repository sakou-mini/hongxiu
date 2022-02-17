package com.donglaistd.jinli.database.entity.race;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.User;
import org.springframework.data.annotation.Transient;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledFuture;

public abstract class RaceBase {

    @Transient
    protected long createTime;
    @Transient
    private String raceImage;

    public abstract String getId();

    public abstract boolean joinRace(User user);

    public abstract boolean quitRace(User user);

    public abstract Constant.RaceType getRaceType();

    public abstract void removeGame(String gameId);

    public abstract int getRaceFee();
    @Transient
    private final Set<WeakReference<? extends ScheduledFuture<?>>> futureList = Collections.newSetFromMap(new WeakHashMap<>());

    public String getRaceImage() {
        return raceImage;
    }

    public void setRaceImage(String raceImage) {
        this.raceImage = raceImage;
    }
}
