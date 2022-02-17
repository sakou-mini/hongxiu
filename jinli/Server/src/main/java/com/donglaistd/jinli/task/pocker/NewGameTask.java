package com.donglaistd.jinli.task.pocker;

import com.donglaistd.jinli.database.entity.game.landlord.Landlords;

public class NewGameTask extends PockBaseTask{

    private NewGameTask(Landlords landlords, long countEndTime) {
        super(landlords, countEndTime);
    }

    public static NewGameTask newInstance(Landlords landlords, long countEndTime){
        return new NewGameTask(landlords,countEndTime);
    }

    @Override
    public void runTask() {
        if(landlords.isEnd()) {
            logger.info("game Over!");
            return;
        }
        if(isRun && !landlords.isEnd()){
            landlords.readyGame(landlords.getConfig().getReadyCountDownTime());
        }
    }
}
