package com.donglaistd.jinli.database.entity.game;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.builder.FriedGoldenFlowerBuilder;
import com.donglaistd.jinli.config.GoldenFlowerRaceConfig;
import com.donglaistd.jinli.database.entity.game.goldenflower.FriedGoldenFlower;
import com.donglaistd.jinli.database.entity.game.texas.RacePokerPlayer;
import com.donglaistd.jinli.database.entity.race.GoldenFlowerRace;
import com.donglaistd.jinli.event.GoldenFlowerEndEvent;
import com.donglaistd.jinli.listener.GoldenFlowerEndListener;
import com.donglaistd.jinli.util.DataManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GoldenFlowerEndListenerTest extends BaseTest {
    @Autowired
    FriedGoldenFlowerBuilder goldenFlowerBuilder;
    @Autowired
    GoldenFlowerEndListener goldenFlowerEndListener;
    @Autowired
    GoldenFlowerRaceConfig config;

    public List<RacePokerPlayer> createRacePokerPlayer(long initCoin,int num,String name){
        List<RacePokerPlayer> players = new ArrayList<>();
        for (int i = 1; i <num ; i++) {
            RacePokerPlayer player1 = new RacePokerPlayer(createTester(200, name+i));
            player1.setInitCoin(initCoin+i);
            players.add(player1);
        }
        return players;
    }

    public GoldenFlowerRace createRace(){
        GoldenFlowerRace goldenFlowerRace = new GoldenFlowerRace(config);
        DataManager.addRace(goldenFlowerRace);
        return goldenFlowerRace;
    }

    @Test
    public void test(){
        GoldenFlowerRace race = createRace();
        FriedGoldenFlower goldenFlower = goldenFlowerBuilder.create(race.getId(),5,5000,20,5);
        DataManager.addGame(goldenFlower);
        //goldenFlower.setCount(new AtomicInteger(5));
        goldenFlower.getWaitPlayers().addAll(createRacePokerPlayer(200,5,"张仁凤"));
        GoldenFlowerEndEvent flowerEndEvent = new GoldenFlowerEndEvent(createRacePokerPlayer(206, 3,"loser"), goldenFlower);
        goldenFlowerEndListener.handle(flowerEndEvent);
    }
}
