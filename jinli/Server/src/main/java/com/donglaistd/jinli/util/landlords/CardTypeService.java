package com.donglaistd.jinli.util.landlords;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.util.CardRulerUtil;

import java.util.List;

import static com.donglaistd.jinli.Constant.LandlordsType.*;
import static com.donglaistd.jinli.util.landlords.LandlordsPokerUtil.*;
public class CardTypeService {

    public static class SingleCardsService {
        public static Constant.LandlordsType getPokerType(List<Card> cards) {
            return Poker_single;
        }
    }

    public static class TwoCardsService {
        public static Constant.LandlordsType getPokerType(List<Card> cards) {
            if(hasSameCountCard(cards, cards.size()))
                return Poker_pair;
            if (isPokerJokerBomb(cards))
                return Poker_jokerBomb;
            return Poker_null;
        }
    }

    public static class ThreeCardsService {
        public static Constant.LandlordsType getPokerType(List<Card> cards) {
            return isPokerThree(cards) ? Poker_three: Poker_null;
        }
    }

    public static class FourCardsService {
        public static Constant.LandlordsType getPokerType(List<Card> cards) {
            if(CardRulerUtil.isBomb(cards)) return Poker_bomb;
            if(isPokerThreeAttachOne(cards)) return Poker_threeAndOne;
            return Poker_null;
        }
    }

    public static class OthersCardsService {
        public static Constant.LandlordsType getPokerType(List<Card> cards) {
            if(isPokerStraight(cards)) return Poker_straight;
            else if (isPokerThreeAttachTwo(cards)) return Poker_threeAndTwo;
            else if (isPokerSerialPair(cards)) return Poker_serialPair;
            else if (isPlane(cards)) return Poker_plane;
            else if (isPlaneAttachSingle(cards)) return Poker_planeAndSingle;
            else if (isPlaneAttachPair(cards)) return Poker_planeAndPair;
            else if (isFourAttachTwo(cards)) return Poker_fourAndTwo;
            return Poker_null;
        }
    }

}
