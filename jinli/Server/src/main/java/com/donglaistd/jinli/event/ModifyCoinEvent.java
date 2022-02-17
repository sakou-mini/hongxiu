package com.donglaistd.jinli.event;

import com.donglaistd.jinli.database.entity.User;

import java.util.HashMap;
import java.util.Map;

public class ModifyCoinEvent implements BaseEvent {
    private final Map<String, Long> modifyCoinMap;

    public ModifyCoinEvent(User user, long amount) {
        this.modifyCoinMap = new HashMap<>();
        modifyCoinMap.put(user.getId(), amount);
        this.runnable = null;
    }

    public ModifyCoinEvent(User user, long amount, Runnable runnable) {
        this.modifyCoinMap = new HashMap<>();
        modifyCoinMap.put(user.getId(), amount);
        this.runnable = runnable;
    }
    public ModifyCoinEvent(String userId, long amount) {
        this.modifyCoinMap = new HashMap<>();
        modifyCoinMap.put(userId, amount);
        this.runnable = null;
    }

    public ModifyCoinEvent(String userId, long amount, Runnable runnable) {
        this.modifyCoinMap = new HashMap<>();
        modifyCoinMap.put(userId, amount);
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    private final Runnable runnable;

    public Map<String, Long> getModifyCoinMap() {
        return modifyCoinMap;
    }

    public ModifyCoinEvent(Map<String, Long> modifyCoinMap) {
        this.modifyCoinMap = modifyCoinMap;
        this.runnable = null;
    }

    public ModifyCoinEvent(Map<String, Long> modifyCoinMap, Runnable runnable) {
        this.modifyCoinMap = modifyCoinMap;
        this.runnable = runnable;
    }
}
