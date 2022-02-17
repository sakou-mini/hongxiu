package com.donglaistd.jinli.listener;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.GoldenFlowerRaceConfig;
import com.donglaistd.jinli.database.entity.game.goldenflower.FriedGoldenFlower;
import com.donglaistd.jinli.database.entity.game.texas.RacePokerPlayer;
import com.donglaistd.jinli.database.entity.race.GoldenFlowerRace;
import com.donglaistd.jinli.event.BaseEvent;
import com.donglaistd.jinli.event.GoldenFlowerEndEvent;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.http.service.CoinFlowService;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.service.GoldenFlowerProcess;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.donglaistd.jinli.constant.GameConstant.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class GoldenFlowerEndListener implements EventListener{
    @Autowired
    GoldenFlowerProcess goldenFlowerProcess;
    @Autowired
    CoinFlowService coinFlowService;
    @Autowired
    GoldenFlowerRaceConfig goldenFlowerRaceConfig;

    @Override
    public boolean handle(BaseEvent event) {
        GoldenFlowerEndEvent e = (GoldenFlowerEndEvent) event;
        FriedGoldenFlower goldenFlower = e.getGoldenFlower();
        List<RacePokerPlayer> outPlayers = e.getPlayers();
        if (Objects.isNull(goldenFlower)) return false;
        int waitPlayerSize = goldenFlower.getWaitPlayers().size();
        GoldenFlowerRace race = (GoldenFlowerRace) DataManager.findRace(goldenFlower.getRaceId());
        if (Objects.isNull(race)) return false;
        Map<Integer, Double> rankCoinAward = race.getConfig().getRankCoinAward();
        goldenFlowerProcess.rankPokerPlayerByInitCoin(waitPlayerSize + GENERAL_NUMBER, outPlayers);
        List<RacePokerPlayer> settlePokerPlayers = new ArrayList<>(outPlayers);
        if (goldenFlower.getFinishCount() == goldenFlower.getGameCount() || waitPlayerSize == ONLY_ONE_PLAYER){
            goldenFlowerProcess.rankPokerPlayerByInitCoin(FIRST_RANK,goldenFlower.getWaitPlayers());
            settlePokerPlayers.addAll(goldenFlower.getWaitPlayers());
            goldenFlower.cancelTimer(false);
            DataManager.removeGame(goldenFlower.getGameId());
            race.removeGame(goldenFlower.getGameId());
        }
        settlePokerPlayers.forEach(p->DataManager.removeUserRace(p.getUserId()));
        getAwardAndBroadCastPlayerRaceResult(settlePokerPlayers, rankCoinAward, goldenFlower.getRewardAmount(),race.getConfig().getRaceFee());
        return true;
    }

    public void getAwardAndBroadCastPlayerRaceResult(List<RacePokerPlayer> players,Map<Integer, Double> rankCoinAward,long rewardAmount,long raceFree){
        for (RacePokerPlayer player : players) {
            int rank = player.getRank();
            Double rate = rankCoinAward.getOrDefault(rank, (double) 0);
            int reward = BigDecimal.valueOf(rewardAmount * rate).intValue();
            if (rate > 0){
                EventPublisher.publish(new ModifyCoinEvent(player.getUser(), reward));
            }
            coinFlowService.setUserCoinFlow(player.getUserId(),raceFree + reward);
            broadCastPlayerRaceResult(player, reward);
        }
    }

    public void broadCastPlayerRaceResult(RacePokerPlayer player,long reward){
        Jinli.GoldenFlowerRaceOverResultBroadcastMessage.Builder message = Jinli.GoldenFlowerRaceOverResultBroadcastMessage.newBuilder();
        message.setRank(player.getRank()).setAwardCoin((int) reward);
        MessageUtil.sendMessage(player.getUserId(), buildReply(message));
    }

    @Override
    public boolean isDisposable() {
        return false;
    }
}
