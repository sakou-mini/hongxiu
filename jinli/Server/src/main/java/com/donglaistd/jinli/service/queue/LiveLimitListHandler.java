package com.donglaistd.jinli.service.queue;

import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.QueueExecute;
import com.donglaistd.jinli.service.statistic.LiveMonitorProcess;
import com.donglaistd.jinli.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class LiveLimitListHandler implements TriggerHandler{
    private static final Logger LOGGER = Logger.getLogger(LiveLimitListHandler.class.getName());
    @Autowired
    DataManager dataManager;
    @Autowired
    LiveMonitorProcess liveMonitorProcess;

    @Override
    public void deal(QueueExecute queueExecute) {
        String liveUserId = queueExecute.getRefId();
        LiveUser liveUser = dataManager.findLiveUser(liveUserId);
        if(!liveUser.isAddWhiteList()){
            LOGGER.info("Anchor limit time, will close the room！ by liveUser=======> "+ liveUser.getId());
           /* Room onlineRoom = DataManager.findOnlineRoom(liveUser.getRoomId());
            if(Objects.nonNull(onlineRoom) && onlineRoom.isLive()){
                 liveMonitorProcess.closeLiveRoom(liveUser.getId(), "",EMPTY_GAME_CLOSE_DELAY_TIME, Constant.EndType.NORMAL_END);
            }*/
        }
    }
}
