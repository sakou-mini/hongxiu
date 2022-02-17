package com.donglai.live.message.services.queue.handler;

import com.donglai.live.process.LiveProcess;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.live.LiveUserService;
import com.donglai.model.db.service.live.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class EndLiveHandler implements TriggerHandler {
    @Autowired
    RoomService roomService;
    @Autowired
    LiveUserService liveUserService;
    @Autowired
    LiveProcess liveProcess;

    @Override
    public void deal(QueueExecute queueExecute) {
        Room room = roomService.findById(queueExecute.getRefId());
        if (Objects.isNull(room)) return;
        liveProcess.endLive(room);
    }
}
