package com.donglaistd.jinli.listener;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.event.BaseEvent;
import com.donglaistd.jinli.event.UserDisconnectEvent;
import com.donglaistd.jinli.processors.handler.QuitRoomRequestHandler;
import com.donglaistd.jinli.service.RoomProcess;
import com.donglaistd.jinli.service.TaskProcess;
import com.donglaistd.jinli.service.UserProcess;
import com.donglaistd.jinli.service.statistic.LiveMonitorProcess;
import com.donglaistd.jinli.service.statistic.UserDataStatisticsProcess;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.ScheduledTaskUtil;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

@Component
public class UserDisconnectListener implements EventListener {
    private final Logger logger = LoggerFactory.getLogger(UserDisconnectListener.class);
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    TaskProcess taskProcess;
    @Autowired
    QuitRoomRequestHandler quitRoomRequestHandler;
    @Autowired
    UserDataStatisticsProcess userDataStatisticsProcess;
    @Autowired
    LiveMonitorProcess liveMonitorProcess;
    @Autowired
    RoomProcess roomProcess;
    @Autowired
    UserProcess userProcess;

    @Value("${disconnect.waiting.time}")
    private long disconnectWaitingTime;
    @Value("${data.rank.coefficient}")
    private int COEFFICIENT;

    @Override
    public boolean handle(BaseEvent event) {
        UserDisconnectEvent disconnectEvent = (UserDisconnectEvent) event;
        User user = disconnectEvent.getUser();
        Channel channel = disconnectEvent.getChannel();
        if (Objects.isNull(user) || Objects.isNull(channel)) return false;
        userDataStatisticsProcess.totalUserOnlineTime(user);
        user.setOnline(false);
        logger.info("user DisConnect  :"+user.getId());
        DataManager.disconnectUser.put(user.getId(), user);
        userProcess.quitRaceIfNotStart(user);
        userProcess.quitRoomIfHasEnterRoom(user);
        //only when player is living ,start disconnectLive
        Optional.ofNullable(dataManager.findLiveUser(user.getLiveUserId())).ifPresent(live -> {
            Room room = DataManager.findOnlineRoom(live.getRoomId());
            if(room!=null && room.isLive()) {
                room.setLiveMusic("");
                dataManager.saveRoom(room);
                logger.info("start countDown closeLiveRoom");
                ScheduledFuture<?> schedule = ScheduledTaskUtil.schedule(() -> {
                    if (DataManager.disconnectUser.containsKey(user.getId())) {
                        Constant.ResultCode resultCode = liveMonitorProcess.closeLiveRoom(user.getLiveUserId(), "", 0, Constant.EndType.READY_TO_CLOSE_LIVE_ROOM);
                        logger.info("close disconnected liveUser: result:" + resultCode);
                        DataManager.closeRoomInfo.remove(room.getId());
                        userDaoService.save(user);
                        dataManager.removeUser(user);
                        DataManager.disconnectUser.remove(user.getId());
                    }
                }, disconnectWaitingTime);
                DataManager.addUserEndLiveTask(user.getId(),schedule);
            }
        });
        dataManager.saveUser(user);
        return true;
    }

    @Override
    public boolean isDisposable() {
        return false;
    }
}
