package com.donglaistd.jinli.listener;

import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.event.BaseEvent;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class ModifyCoinListener implements SyncEventListener {
    private static final java.util.logging.Logger logger = Logger.getLogger(ModifyCoinListener.class.getName());
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    DataManager dataManager;

    @Override
    public synchronized boolean handle(BaseEvent event) {
        var e = (ModifyCoinEvent) event;
        var modifyCoinMap = e.getModifyCoinMap();
        for (var entry : modifyCoinMap.entrySet()) {
            var user = dataManager.findOnlineUser(entry.getKey());
            if (user == null) {
                logger.fine("modify offline user coin, value:" + entry.getValue());
                userDaoService.increaseGameCoin(entry.getKey(), entry.getValue()).getModifiedCount();
            } else {
                logger.fine("modify online user coin, before:" + user.getGameCoin() + ", modify value:" + entry.getValue() +"modify after is :" + (user.getGameCoin() + entry.getValue()));
                user.setGameCoin(user.getGameCoin() + entry.getValue());
                dataManager.saveUser(user);
            }
        }
        var runnable = e.getRunnable();
        if (runnable != null) {
            runnable.run();
        }
        return true;
    }

    @Override
    public boolean isDisposable() {
        return false;
    }
}
