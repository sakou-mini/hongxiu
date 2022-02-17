package com.donglaistd.jinli.listener;

import com.donglaistd.jinli.database.entity.race.TexasRace;
import com.donglaistd.jinli.event.AddTexasEvent;
import com.donglaistd.jinli.event.BaseEvent;
import com.donglaistd.jinli.service.TexasRaceProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddTexasListener implements EventListener {
    @Autowired
    private TexasRaceProcess texasRaceProcess;
    @Override
    public boolean handle(BaseEvent event) {
        AddTexasEvent e = (AddTexasEvent) event;
        TexasRace race = e.getRace();
        texasRaceProcess.createGame(race);
        return true;
    }

    @Override
    public boolean isDisposable() {
        return false;
    }
}
