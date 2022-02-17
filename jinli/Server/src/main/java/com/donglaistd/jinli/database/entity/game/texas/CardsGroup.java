package com.donglaistd.jinli.database.entity.game.texas;


public class CardsGroup<T, C> {
    private final T texasType;
    private final C cardsList;

    public T getTexasType() {
        return texasType;
    }

    public C getCardsList() {
        return cardsList;
    }

    public CardsGroup(T texasType, C cardsList) {
        assert texasType != null;
        assert cardsList != null;
        this.texasType = texasType;
        this.cardsList = cardsList;
    }
    @Override
    public int hashCode() {
        return texasType.hashCode() ^ cardsList.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CardsGroup)) return false;
        CardsGroup group = (CardsGroup) o;
        return this.texasType.equals(group.getTexasType()) &&
                this.cardsList.equals(group.getCardsList());
    }
}
