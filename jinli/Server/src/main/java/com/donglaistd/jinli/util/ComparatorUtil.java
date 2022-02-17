package com.donglaistd.jinli.util;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.game.landlord.PokerPlayer;
import com.donglaistd.jinli.database.entity.game.texas.RacePokerPlayer;

import java.util.Comparator;

public class ComparatorUtil {
    public static Comparator<Card> getCardComparatorForGameType(Constant.GameType gameType){
        return (card1, card2) -> {
            int cardNumber1 = CardRulerUtil.getCardPointByGameType(card1,gameType);
            int cardNumber2 = CardRulerUtil.getCardPointByGameType(card2,gameType);
            if (cardNumber1 == cardNumber2) {
                return -(card2.getCardType().getNumber() - card1.getCardType().getNumber());
            } else {
                return cardNumber2 - cardNumber1;
            }
        };
    }

    public static Comparator<RacePokerPlayer> getRacePokerPlayerComparator(){
        return (player1, player2) -> (int) (player2.getInitCoin() - player1.getInitCoin());
    }

    public static Comparator<PokerPlayer> getLandlordsPokerPlayerRankComparator(){
        return (player1, player2) -> {
            long score1 = player1.getInitCoin();
            long score2 = player2.getInitCoin();
            if(score1 == score2){
                int winCount1 = player1.getWinRoundRecord().size();
                int winCount2= player2.getWinRoundRecord().size();
                if(winCount1 == winCount2){
                    return player2.sumWinRound() - player1.sumWinRound();
                }else{
                    return winCount2 - winCount1;
                }
            }
            return (int) (score2 - score1);
        };
    }

    public static Comparator<Room> getRoomRecommendComparator(Constant.PlatformType platform){
        return (room1, room2) -> {
            int sortNum1 = room1.getPlatformRecommendWeight(platform);
            int sortNum2 = room2.getPlatformRecommendWeight(platform);
            if(sortNum1<=1 && sortNum2<=1) return Boolean.compare( room2.isHot(), room1.isHot());
            return sortNum2 - sortNum1;
        };
    }

    public static Comparator<Constant.PlatformType> getPlatformComparator(){
        return Comparator.comparing(Constant.PlatformType::getNumber);
    }
}
