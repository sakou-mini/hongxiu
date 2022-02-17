package com.donglaistd.jinli.listener;

import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.event.BankerQuitEvent;
import com.donglaistd.jinli.event.BaseEvent;
import com.donglaistd.jinli.processors.handler.QuitRoomRequestHandler;
import com.donglaistd.jinli.service.RoomProcess;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.logging.Logger;

@Component
public class BankerQuitListener implements EventListener {
    private Logger logger = Logger.getLogger(BankerQuitListener.class.getName());

    @Autowired
    private QuitRoomRequestHandler quitRoomRequestHandler;
    @Autowired
    private DataManager dataManager;
    @Autowired
    RoomProcess roomProcess;
    @Override
    public boolean handle(BaseEvent event) {
        var e = (BankerQuitEvent) event;
        User user = dataManager.findUser(e.getUser().getId());
        Room room = DataManager.findOnlineRoom(e.getRoomId());
        Channel userChannel = DataManager.getUserChannel(user.getId());
        if(Objects.isNull(room)) return false;
        logger.info("auto quit Game!");
        room.removeAudience(user);
        room.removeConnectLiveCodeByUser(user);
        roomProcess.sendQuiteRoomMessage(room,user);
        user.setQuitingBanker(false);
        user.setQuiteGame(false);
        user.cleanCurrentRoomId();
        dataManager.saveUser(user);
        return true;
    }

    @Override
    public boolean isDisposable() {
        return false;
    }
}
