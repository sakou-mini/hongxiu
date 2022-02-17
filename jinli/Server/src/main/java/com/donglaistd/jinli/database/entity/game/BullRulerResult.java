package com.donglaistd.jinli.database.entity.game;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.exception.CardParameterException;
import com.donglaistd.jinli.util.CardRulerUtil;
import com.donglaistd.jinli.util.ComparatorUtil;

import java.util.ArrayList;
import java.util.List;

public class BullRulerResult {

    private final Constant.BullType bullType;

    private final Card maxCard;

    private BullRulerResult(Constant.BullType bullType, Card maxCard) {
        this.bullType = bullType;
        this.maxCard = maxCard;
    }

    public Card getMaxCard() {
        return maxCard;
    }

    public Constant.BullType getBullType() {
        return bullType;
    }

    public static BullRulerResult getBullResult(List<Card> cards) throws CardParameterException {
        var cardList = new ArrayList<>(cards);
        if (cardList.size() != 5) {
            throw new CardParameterException("The BullCards Required 5 size");
        }
        Card maxCard = CardRulerUtil.getMaxCardFromList(cardList, ComparatorUtil.getCardComparatorForGameType(Constant.GameType.NIUNIU));
        Constant.BullType figureBullType = CardRulerUtil.getFigureBullType(cardList);
        if (CardRulerUtil.isSmallBull(cardList))
            return new BullRulerResult(Constant.BullType.SmallBull_5, maxCard);
        else if (CardRulerUtil.isBomb(cardList))
            return new BullRulerResult(Constant.BullType.Bomb, maxCard);
        else if (!figureBullType.equals(Constant.BullType.UNRECOGNIZED))
            return new BullRulerResult(figureBullType, maxCard);
        else {
            return new BullRulerResult(CardRulerUtil.getBullType(cardList), maxCard);
        }
    }

}
