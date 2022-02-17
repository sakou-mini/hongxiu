package com.donglaistd.jinli.util.landlords;

import com.donglaistd.jinli.database.entity.game.landlord.PokerPlayer;
import com.donglaistd.jinli.database.entity.race.LandlordsRace;
import com.donglaistd.jinli.util.ComparatorUtil;
import com.donglaistd.jinli.util.RandomUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.constant.GameConstant.FIRST_RANK;
import static com.donglaistd.jinli.constant.GameConstant.THIRD_RANK;

public class LandlordsRankUtil {
    public static PokerPlayer getFirstRankPlayer(List<PokerPlayer> players){
        List<PokerPlayer> tempPlayer = new ArrayList<>(players);
        tempPlayer.sort(ComparatorUtil.getLandlordsPokerPlayerRankComparator());
        PokerPlayer first = tempPlayer.get(0);
        List<PokerPlayer> firstPlayers = new ArrayList<>();
        firstPlayers.add(first);
        for (int i = 1; i < tempPlayer.size() ; i++) {
            if(sameAsFirstPlayer(first,tempPlayer.get(i)))
                firstPlayers.add(tempPlayer.get(i));
        }
        if(firstPlayers.size() > 1) first = randomFirstPlayer(firstPlayers);
        first.setRank(FIRST_RANK);
        return first;
    }
    private static PokerPlayer randomFirstPlayer(List<PokerPlayer> firstPlayers){
        return firstPlayers.get(RandomUtil.getRandomInt(0, firstPlayers.size() - 1, null));
    }

    private static boolean sameAsFirstPlayer(PokerPlayer firstPlayer, PokerPlayer player){
        return firstPlayer.getInitCoin() == player.getInitCoin() && firstPlayer.getWinRoundRecord().size() == player.getWinRoundRecord().size() &&
                firstPlayer.sumWinRound() == player.sumWinRound();
    }


    public static void rankRaceWeedOutPlayers(List<PokerPlayer> weedOutPlayers, LandlordsRace race){
        int weekOutRank = race.getJoinQueues().size() - weedOutPlayers.size() + 1;
        rankPlayersByStartRank(weedOutPlayers,weekOutRank);
    }

    public static void rankPlayersByStartRank(List<PokerPlayer> pokerPlayers, int startRank){
        Map<Long, List<PokerPlayer>> scoreGroup = pokerPlayers.stream().collect(Collectors.groupingBy(PokerPlayer::getInitCoin));
        List<Long> scoreRank = scoreGroup.keySet().stream().sorted(Comparator.comparing(Long::intValue).reversed()).collect(Collectors.toList());
        scoreGroup.forEach((score,players)-> players.forEach(player -> player.setRank(scoreRank.indexOf(score) + startRank)));
    }

    public static void rankWithRaceSettle(List<PokerPlayer> pokerPlayers){
        rankPlayersByStartRank(pokerPlayers,FIRST_RANK);
        long firstCount = pokerPlayers.stream().filter(player -> player.getRank() == FIRST_RANK).count();
        if(firstCount > 1){
            pokerPlayers.stream().filter(player -> player.getRank() > FIRST_RANK).forEach(player -> player.setRank(THIRD_RANK));
        }
    }
}
