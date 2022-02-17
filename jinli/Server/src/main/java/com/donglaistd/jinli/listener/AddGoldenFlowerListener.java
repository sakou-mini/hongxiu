package com.donglaistd.jinli.listener;

import com.donglaistd.jinli.database.entity.race.GoldenFlowerRace;
import com.donglaistd.jinli.event.AddGoldenFlowerEvent;
import com.donglaistd.jinli.event.BaseEvent;
import com.donglaistd.jinli.service.GoldenFlowerProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddGoldenFlowerListener implements EventListener{
    @Autowired
    private GoldenFlowerProcess goldenFlowerProcess;
    @Override
    public boolean handle(BaseEvent event) {
        AddGoldenFlowerEvent e = (AddGoldenFlowerEvent) event;
        GoldenFlowerRace goldenFlowerRace = e.getGoldenFlowerRace();
        goldenFlowerProcess.createGame(goldenFlowerRace);
        return true;
    }

    @Override
    public boolean isDisposable() {
        return false;
    }
}
