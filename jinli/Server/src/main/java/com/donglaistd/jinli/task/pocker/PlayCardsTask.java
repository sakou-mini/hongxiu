package com.donglaistd.jinli.task.pocker;

import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.game.landlord.Landlords;
import com.donglaistd.jinli.database.entity.game.landlord.PokerPlayer;
import com.donglaistd.jinli.database.entity.game.landlord.PokerRecord;
import com.donglaistd.jinli.util.landlords.LandlordsPokerPlayCardsUtil;

import java.util.List;
import java.util.Objects;

public class PlayCardsTask extends PockBaseTask{

    private PlayCardsTask(PokerPlayer pokerPlayer, Landlords landlords, long countDownTime) {
        super(pokerPlayer, landlords, countDownTime);
    }

    public static PlayCardsTask newInstance(PokerPlayer pokerPlayer, Landlords landlords, long countDownTime){
        return new PlayCardsTask(pokerPlayer, landlords,countDownTime);
    }



    @Override
    public void runTask() {
        if(landlords.isEnd()) {
            logger.info("game Over!");
            return;
        }
        if(isRun && !landlords.isEnd()){
            List<Card> cards ;
            if(landlords.isNewRound()){
                cards = LandlordsPokerPlayCardsUtil.playMinCardCombination(landlords.getUserCards(pokerPlayer.getUserId()));
                logger.fine(pokerPlayer.getUser().getDisplayName() + "玩家自己的回合，任意出牌！" + cards);
            }else {
                cards = LandlordsPokerPlayCardsUtil.autoPlayCards(landlords.getLastPlayRecord(),landlords.getUserCards(pokerPlayer.getUserId()));
            }
            PokerRecord pokerRecord = PokerRecord.newInstance(cards, pokerPlayer.getUserId());
            landlords.playCards(pokerRecord,pokerPlayer);
        }
    }

    public boolean isNewRound(){
        PokerRecord lastPlayRecord = landlords.getLastPlayRecord();
        return lastPlayRecord == null || Objects.equals(pokerPlayer.getUserId(),lastPlayRecord.getUserId());
    }
}
