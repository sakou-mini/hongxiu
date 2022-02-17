package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.database.entity.game.Longhu;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static com.donglaistd.jinli.Game.BetType.*;

@Component
public class LonghuBuilder {
    @Value("${longhu.betting.long.limit}")
    private int longLimit;
    @Value("${longhu.betting.hu.limit}")
    private int huLimit;
    @Value("${longhu.betting.draw.limit}")
    private int drawLimit;
    @Value("${longhu.game.history.count}")
    private int historyCount;

    @Value("${longhu.minimal.deck.card}")
    private int longhuMinimalDeckCard;

    @Value("${longhu.game.finish.delay}")
    private int longhuFinishDelay;

    @Value("${longhu.betting.time}")
    private long bettingTime;

    public Longhu create() {
        Longhu longhu = new Longhu(DeckBuilder.getNoJokerDeck(4), longhuMinimalDeckCard);
        var betLimitMap = new HashMap<Game.BetType, Integer>();
        betLimitMap.put(LONG, longLimit);
        betLimitMap.put(HU, huLimit);
        betLimitMap.put(LONGHU_DRAW, drawLimit);
        longhu.setBetLimitMap(betLimitMap);
        longhu.setHistoryCount(historyCount);
        longhu.setDelayFinishTime(longhuFinishDelay);
        longhu.setBettingTime(bettingTime);
        return longhu;
    }
}
