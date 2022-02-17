package com.donglaistd.jinli.listener;

import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.event.BaseEvent;
import com.donglaistd.jinli.event.ModifyUserResourceEvent;
import com.donglaistd.jinli.service.UserOperationService;
import com.donglaistd.jinli.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class ModifyUserResourceEventListener implements SyncEventListener{
    private static final java.util.logging.Logger logger = Logger.getLogger(ModifyCoinListener.class.getName());
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    UserOperationService userOperationService;

    @Override
    public synchronized boolean handle(BaseEvent event) {
        var e = (ModifyUserResourceEvent) event;
        String userId = e.getUserId();
        User user = dataManager.findOnlineUser(e.getUserId());
        long amount = e.getAmount();
        boolean isOnline = user != null;
        switch (e.getModifyType()){
            case goldBean:
                modifyGoldBean(userId,amount);
                break;
            case gameCoin:
                modifyGameCoin(userId,amount);
                break;
            case exp:
                modifyExp(userId, amount);
        }
        return true;
    }

    private void modifyGoldBean(String userId,long amount){
        User user = dataManager.findOnlineUser(userId);
        if(user == null){
            userDaoService.increaseGoldBean(userId, amount);
        }else{
            logger.fine("modify online user goldBean, before:" + user.getGoldBean() + ", modify value:" + amount);
            user.addGoldBean(amount);
            dataManager.saveUser(user);
        }
    }

    private void modifyGameCoin(String userId,long amount){
        User user = dataManager.findOnlineUser(userId);
        if(user == null){
            userDaoService.increaseGameCoin(userId, amount);
        }else{
            logger.fine("modify online user gameCoin, before:" + user.getGoldBean() + ", modify value:" + amount);
            user.addGameCoin(amount);
            dataManager.saveUser(user);
        }
    }

    private void modifyExp(String userId,long amount){
        User user = dataManager.findOnlineUser(userId);
        if(user == null){
            user = userDaoService.findById(userId);
            userOperationService.updateUserExp(user, amount);
            //user.updateLevel(amount);
            userDaoService.save(user);
        }else{
            logger.fine("modify online user exp, before:" + user.getExperience() + ", modify value:" + amount);
            userOperationService.updateUserExp(user, amount);
            //user.updateLevel(amount);
            dataManager.saveUser(user);
        }
    }
    @Override
    public boolean isDisposable() {
        return false;
    }
}
