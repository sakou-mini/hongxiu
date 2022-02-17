package com.donglaistd.jinli.util;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.database.entity.game.landlord.PokerPlayer;
import com.donglaistd.jinli.util.landlords.LandlordsRankUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PokerPlayerRankUtilTest extends BaseTest {

    @Test
    public void getFirstRankTest() {
        List<PokerPlayer> pokerPlayers = new ArrayList<>(3);
        PokerPlayer pokerPlayer = new PokerPlayer(createTester(10, "张三"), 1, 1200);
        pokerPlayer.addWinRoundRecord(1);
        pokerPlayer.addWinRoundRecord(2);
        pokerPlayers.add(pokerPlayer);

        PokerPlayer pokerPlayer2 = new PokerPlayer(createTester(10, "李四"), 1, 1200);
        pokerPlayer2.addWinRoundRecord(1);
        pokerPlayer2.addWinRoundRecord(2);
        pokerPlayers.add(pokerPlayer2);

        PokerPlayer pokerPlayer3 = new PokerPlayer(createTester(10, "王五"), 1, 1200);
        pokerPlayer3.addWinRoundRecord(4);
        pokerPlayer3.addWinRoundRecord(5);
        pokerPlayers.add(pokerPlayer3);

        PokerPlayer firstPlayer = LandlordsRankUtil.getFirstRankPlayer(pokerPlayers);
        Assert.assertEquals(firstPlayer, pokerPlayer3);
    }

    @Test
    public void raceRankTest(){
        List<PokerPlayer> pokerPlayers = new ArrayList<>(3);
        PokerPlayer pokerPlayer1 = new PokerPlayer(createTester(10, "张三"), 1, 1200);
        pokerPlayer1.addWinRoundRecord(1);
        pokerPlayer1.addWinRoundRecord(2);
        PokerPlayer pokerPlayer2 = new PokerPlayer(createTester(10, "李四"), 1, 1200);
        pokerPlayer2.addWinRoundRecord(1);
        pokerPlayer2.addWinRoundRecord(2);
        PokerPlayer pokerPlayer3 = new PokerPlayer(createTester(10, "王五"), 1, 1100);
        pokerPlayer3.addWinRoundRecord(4);
        pokerPlayer3.addWinRoundRecord(5);
        pokerPlayers.add(pokerPlayer1);
        pokerPlayers.add(pokerPlayer2);
        pokerPlayers.add(pokerPlayer3);
        LandlordsRankUtil.rankWithRaceSettle(pokerPlayers);
        Assert.assertEquals(1, pokerPlayer1.getRank());
        Assert.assertEquals(1, pokerPlayer2.getRank());
        Assert.assertEquals(3, pokerPlayer3.getRank());
    }

}
