package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.database.entity.game.goldenflower.FriedGoldenFlower;
import com.donglaistd.jinli.database.entity.game.goldenflower.GoldenFlowerConfig;
import com.donglaistd.jinli.util.TexasUtil;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FriedGoldenFlowerBuilder {
    @Autowired
    private GoldenFlowerConfig config;

    public FriedGoldenFlower create(String raceId,int gameCount,int rewardAmount,int cost,int joinPeopleNum) {
        FriedGoldenFlower flower = new FriedGoldenFlower(RandomUtils.nextInt(0, joinPeopleNum), TexasUtil.getStack(joinPeopleNum), raceId, config, gameCount);
        flower.setDeck(DeckBuilder.getOneNoJokerDeck());
        flower.setRewardAmount(rewardAmount);
        flower.setCost(cost);
        return flower;
    }
}
