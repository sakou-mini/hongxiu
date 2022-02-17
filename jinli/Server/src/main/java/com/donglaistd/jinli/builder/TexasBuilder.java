package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.database.entity.game.texas.Texas;
import com.donglaistd.jinli.config.TexasConfig;
import com.donglaistd.jinli.util.TexasUtil;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TexasBuilder {
    @Autowired
    private TexasConfig textConfig;
    public Texas create(int maxPlayers, String raceId) {
        //textConfig.setMaxPlayers(maxPlayers);
        Texas texas = new Texas(DeckBuilder.getOneNoJokerDeck(), textConfig, raceId);
        texas.setMaxPlayers(maxPlayers);
        texas.setFreeSeatStack(TexasUtil.getStack(maxPlayers));
        texas.setDealer(RandomUtils.nextInt(0, maxPlayers));
        return texas;
    }
}
