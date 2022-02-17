package com.donglaistd.jinli.task.pocker;

import com.donglaistd.jinli.database.entity.game.landlord.Landlords;
import com.donglaistd.jinli.database.entity.game.landlord.PokerPlayer;
import com.donglaistd.jinli.util.RandomUtil;

import java.util.List;

public class RedoubleTask extends PockBaseTask{
    private RedoubleTask(List<PokerPlayer> pokerPlayerList, Landlords landlords, long countDownTime) {
        super(pokerPlayerList,landlords,countDownTime);
    }
    private RedoubleTask(PokerPlayer pokerPlayer, Landlords landlords, long countEndTime) {
        super(pokerPlayer, landlords, countEndTime);
    }

    public static RedoubleTask newInstance(PokerPlayer pokerPlayer, Landlords landlords, long countEndTime) {
        return  new RedoubleTask(pokerPlayer, landlords,countEndTime);
    }

    public static RedoubleTask newInstance(List<PokerPlayer> pokerPlayers, Landlords landlords, long countEndTime) {
        return  new RedoubleTask(pokerPlayers, landlords,countEndTime);
    }

    @Override
    public void runTask() {
        if(landlords.isEnd()) {
            logger.info("game Over!");
            return;
        }
        if(isRun && !landlords.isEnd()){
            for (PokerPlayer player : pokerPlayers) {
                landlords.plusRate(player,false);
            }
        }
    }
}
