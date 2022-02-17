package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.config.LandlordGameConfig;
import com.donglaistd.jinli.database.entity.game.landlord.Landlords;
import com.donglaistd.jinli.database.entity.game.landlord.PokerPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LandlordsBuilder {

    @Autowired
    LandlordGameConfig landlordGameConfig;

    public Landlords create(List<PokerPlayer> players, String raceId){
        Landlords landlords = Landlords.newInstance(DeckBuilder.getJokerDeck(), players, raceId);
        landlords.setConfig(landlordGameConfig);
        return landlords;
    }
}
