package com.donglaistd.jinli.task.pocker;

import com.donglaistd.jinli.database.entity.game.landlord.Landlords;

public class DelayRunTask extends PockBaseTask{
    private final Runnable runnable;

    private DelayRunTask(Landlords landlords, Runnable runnable, long countDownTime) {
        super(landlords, countDownTime);
        this.runnable = runnable;
    }

    public static DelayRunTask newInstance(Landlords landlords,Runnable runnable,long time){
        return new DelayRunTask(landlords,runnable,time);
    }

    @Override
    public void runTask() {
        if(landlords.isEnd()) {
            logger.info("game Over!");
            return;
        }
        if(isRun){
            runnable.run();
        }
    }
}
