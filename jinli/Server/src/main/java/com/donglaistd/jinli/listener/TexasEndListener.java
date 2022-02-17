package com.donglaistd.jinli.listener;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.TexasRaceBuilder;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.texas.RacePokerPlayer;
import com.donglaistd.jinli.database.entity.game.texas.Texas;
import com.donglaistd.jinli.database.entity.race.TexasRace;
import com.donglaistd.jinli.event.BaseEvent;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.event.TexasEndEvent;
import com.donglaistd.jinli.http.service.CoinFlowService;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.constant.GameConstant.FIRST_RANK;
import static com.donglaistd.jinli.constant.GameConstant.SECOND_RANK;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class TexasEndListener implements EventListener {
    Logger logger = Logger.getLogger(TexasEndListener.class.getName());
    @Autowired
    CoinFlowService coinFlowService;

    @Override
    public boolean handle(BaseEvent e) {
        TexasEndEvent event = (TexasEndEvent) e;
        List<RacePokerPlayer> players = event.getPlayers();
        Texas texas = event.getTexas();
        if (Objects.isNull(texas)) return false;
        int waitPlayerSize = texas.getWaitPlayers().size();
        TexasRace race = (TexasRace) DataManager.findRace(texas.getRaceId());
        if (Objects.isNull(race)) return false;
        Map<Integer, Double> rankCoinAward = race.getConfig().getRankCoinAward();
        players.forEach(p -> DataManager.removeUserRace(p.getUserId()));
        rankSettlePlayer(players, waitPlayerSize);
        //settle out players
        settleOutPlayer(players, rankCoinAward, texas.getRewardAmount());
        //settle firstPlayers
        if(waitPlayerSize ==1 ){
            settleFirstPlayer(texas.getWaitPlayers(), rankCoinAward, texas.getRewardAmount());
            texas.cancelIncreaseBetTimer();
            DataManager.removeGame(texas.getGameId());
            race.removeGame(texas.getGameId());
        }
        return true;
    }

    @Override
    public boolean isDisposable() {
        return false;
    }


    public void settleOutPlayer(List<RacePokerPlayer> settlePlayers, Map<Integer, Double> rankCoinAward,int rewardAmount ){
        Map<Integer, List<RacePokerPlayer>> rankUsers = settlePlayers.stream().collect(Collectors.groupingBy(RacePokerPlayer::getRank));
        rankUsers.forEach((rank,players)->{
            //1.特殊情况处理，如果 均为第二名，则 总奖金为 三名  + 二名
            double rate = rankCoinAward.getOrDefault(rank,0d);
            if(isSameRankForTwo(rankUsers)){
                rate = rankCoinAward.get(SECOND_RANK) + rankCoinAward.get(SECOND_RANK);
            }
            int amount = BigDecimal.valueOf(rewardAmount*rate).divide(BigDecimal.valueOf(players.size()), RoundingMode.DOWN).intValue();
            players.forEach(p -> assignmentReward(p.getRank(), amount, p.getUser()));
        });
    }

    public void settleFirstPlayer(List<RacePokerPlayer> waitPlayers,Map<Integer, Double> rankCoinAward,int rewardAmount){
        int amount = BigDecimal.valueOf(rankCoinAward.get(FIRST_RANK) * rewardAmount).intValue();
        waitPlayers.forEach(p -> {
            p.setRank(FIRST_RANK);
            DataManager.removeUserRace(p.getUserId());
            assignmentReward(p.getRank(), amount, p.getUser());
            logger.info(p.getUser().getDisplayName()+"获得了第1名"+"获得奖励："+amount);
        });
    }

    @Transactional
    void assignmentReward(int rank, int reward, User user) {
        if (reward > 0) {
            EventPublisher.publish(new ModifyCoinEvent(user, reward));
        }
        coinFlowService.setUserCoinFlow(user.getId(), TexasRaceBuilder.getFee() + reward);
        broadCastPlayerRaceResult(user.getId(), rank, reward);
    }
    public boolean isSameRankForTwo(Map<Integer, List<RacePokerPlayer>> rankUsers){
        return rankUsers.size() == 1 && rankUsers.containsKey(SECOND_RANK);
    }

    public void rankSettlePlayer(List<RacePokerPlayer> players,int leftPlayer){
        Map<Long, List<RacePokerPlayer>> scoreGroup = players.stream().collect(Collectors.groupingBy(RacePokerPlayer::getInitCoin));
        List<Long> scoreRank = scoreGroup.keySet().stream().sorted(Comparator.comparing(Long::intValue).reversed()).collect(Collectors.toList());
        scoreGroup.forEach((coin, tempPlayers) -> tempPlayers.forEach(player -> player.setRank(scoreRank.indexOf(coin) + 1 + leftPlayer)));
    }

    public void broadCastPlayerRaceResult(String userId, int rank, int winAmount) {
        Jinli.TexasRaceOverResultBroadcastMessage.Builder builder = Jinli.TexasRaceOverResultBroadcastMessage.newBuilder().setRank(rank).setAwardCoin(winAmount);
        MessageUtil.sendMessage(userId, buildReply(builder));
    }

}
