package com.donglai.live.message.services.queue.handler;

import com.donglai.live.process.QueueProcess;
import com.donglai.live.process.SportRaceProcess;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.entity.sport.SportEvent;
import com.donglai.model.db.service.common.QueueExecuteService;
import com.donglai.model.db.service.sport.SportEventService;
import com.donglai.model.dto.ExtraSportRaceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class SportEventHandler  implements TriggerHandler {
    @Autowired
    SportRaceProcess sportRaceProcess;
    @Autowired
    SportEventService sportEventService;
    @Autowired
    QueueProcess queueProcess;
    @Autowired
    QueueExecuteService queueExecuteService;

    @Override
    public void deal(QueueExecute queueExecute) {
        List<ExtraSportRaceDTO> sportRaceList = sportRaceProcess.getSportRaceList();
        Set<SportEvent> sportEvents = new HashSet<>();
        for (ExtraSportRaceDTO extraSportRaceDTO : sportRaceList) {
            sportEvents.add(new SportEvent(extraSportRaceDTO));
        }
        sportEventService.saveAll(sportEvents);
        queueExecuteService.del(queueExecute);
        queueProcess.initSportEventListQueue();
        log.info("更新赛事列表完毕===========");
    }
}
