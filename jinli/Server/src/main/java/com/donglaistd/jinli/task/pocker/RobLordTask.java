package com.donglaistd.jinli.task.pocker;

import com.donglaistd.jinli.database.entity.game.landlord.Landlords;
import com.donglaistd.jinli.database.entity.game.landlord.PokerPlayer;
import com.donglaistd.jinli.util.RandomUtil;

import static com.donglaistd.jinli.constant.GameConstant.LANDLORD_MAX_GRAB_ROUND;


public class RobLordTask extends PockBaseTask{

    public RobLordTask(PokerPlayer pokerPlayer, Landlords landlords, long countEndTime) {
        super(pokerPlayer, landlords, countEndTime);
    }

    public static RobLordTask newInstance(PokerPlayer pokerPlayer, Landlords landlords, long countEndTime) {
        return  new RobLordTask(pokerPlayer, landlords,countEndTime);
    }

    public void runTask(){
        if(landlords.isEnd()) {
            logger.info("game Over!");
            return;
        }
        if(isRun && !landlords.isEnd()){
            boolean isGrab = RandomUtil.randomBool(null);
            if(landlords.getGrabLandlordRound() == LANDLORD_MAX_GRAB_ROUND && landlords.countGrabLordRecord()<=0) {
                isGrab = true;
            }
            landlords.grabLandlord(pokerPlayer,isGrab);
        }
    }
}
