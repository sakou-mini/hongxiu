package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.util.CardRulerUtil;

import java.io.Serializable;
import java.util.Objects;

public class Card implements Serializable,Comparable<Card>{
    private final Constant.CardNumber cardNumber;
    private final Constant.CardType cardType;

    public Card(Constant.CardNumber cardNumber, Constant.CardType cardValue) {
        this.cardNumber = cardNumber;
        this.cardType = cardValue;
    }

    public Constant.CardNumber getCardValue() {
        return cardNumber;
    }

    public Constant.CardType getCardType() {
        return cardType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return cardNumber == card.cardNumber && cardType == card.cardType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber, cardType);
    }

    @Override
    public String toString() {
        return "Card{" + "cardNumber=" + cardNumber + ", cardValue=" + cardType + '}';
    }

    public int getCardIntValue() {
        int number = cardNumber.getNumber();
        if (number <= 8) {
            return number + 1;
        } else if (cardNumber.equals(Constant.CardNumber.Ace)) {
            return 1;
        } else {
            return 10;
        }
    }

    public boolean isFaceCard() {
        int cardPoint = cardNumber.getNumber();
        return cardPoint >= Constant.CardNumber.Jack_VALUE && cardPoint <= Constant.CardNumber.King_VALUE;
    }

    public boolean greaterThanOtherCard(Card card, Constant.GameType gameType) {
        int card1Point = CardRulerUtil.getCardPointByGameType(this, gameType);
        int card2Point = CardRulerUtil.getCardPointByGameType(card, gameType);
        if (card1Point == card2Point) {
            return getCardType().getNumber() < card.getCardType().getNumber();
        }
        return card1Point > card2Point;
    }

    public int getCardNumber() {
        return cardNumber.getNumber();
    }
    public int getCardTypeValue() {
        return cardType.getNumber();
    }
    @Override
    public int compareTo(Card card) {
        return this.getCardValue().getNumber() - card.getCardValue().getNumber();
    }
}
