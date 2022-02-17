package com.donglaistd.jinli.database.entity.game.landlord;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.util.MessageUtil;
import com.donglaistd.jinli.util.landlords.LandlordsPokerUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PokerRecord {

    private final String userId;

    private final Constant.LandlordsType pokerType;

    private List<Card> baseCard;

    private List<Card> attachCards;


    private PokerRecord(Constant.LandlordsType pokerType, List<Card> cards, String userId) {
        this.pokerType = pokerType;
        this.userId = userId;
        splitAttachCards(cards);
    }

    public static PokerRecord newInstance(Constant.LandlordsType pokerType, List<Card> cards, String userId) {
        return new PokerRecord(pokerType, cards, userId);
    }

    public static PokerRecord newInstance(List<Card> cards, String userId){
        Constant.LandlordsType pokerType = LandlordsPokerUtil.checkPokerType(cards);
        return new PokerRecord(pokerType, cards, userId);
    }

    public Constant.LandlordsType getPokerType() {
        return pokerType;
    }

    public List<Card> getCards() {
        return Stream.of(baseCard,attachCards).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public String getUserId() {
        return userId;
    }

    public List<Card> getBaseCard() {
        return baseCard;
    }

    public int getCardsSize(){
        return baseCard.size() + attachCards.size();
    }

    public Jinli.LandlordCardRecord toProto() {
        List<Game.Card> protoCards = MessageUtil.getJinliCard(getCards());
        return Jinli.LandlordCardRecord.newBuilder().addAllCards(protoCards).setType(pokerType).build();
    }

    private void splitAttachCards(List<Card> cards){
        this.attachCards = LandlordsPokerUtil.splitAttachCards(cards);
        this.baseCard = new ArrayList<>(cards);
        this.baseCard.removeAll(this.attachCards);
    }
}
