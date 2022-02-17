package com.donglaistd.jinli.listener;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.LandlordsBuilder;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.game.landlord.Landlords;
import com.donglaistd.jinli.database.entity.game.landlord.PokerPlayer;
import com.donglaistd.jinli.database.entity.race.LandlordsRace;
import com.donglaistd.jinli.database.entity.race.UserRace;
import com.donglaistd.jinli.event.BaseEvent;
import com.donglaistd.jinli.event.LandlordsEndEvent;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.http.service.CoinFlowService;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.service.LandlordRaceProcess;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.MessageUtil;
import com.donglaistd.jinli.util.ScheduledTaskUtil;
import com.donglaistd.jinli.util.landlords.LandlordsRankUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.RaceStep.Race_FirstRound;
import static com.donglaistd.jinli.Constant.RaceStep.Race_LastRound;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class LandlordsEndListener implements EventListener {

    private static final Logger logger = Logger.getLogger(LandlordsEndListener.class.getName());
    @Autowired
    DataManager dataManager;

    @Autowired
    LandlordsBuilder landlordsBuilder;

    @Autowired
    LandlordRaceProcess landlordRaceProcess;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    CoinFlowService coinFlowService;

    @Override
    public boolean handle(BaseEvent event) {
        logger.info("game end....");
        var e = (LandlordsEndEvent) event;
        var game = e.landlords;
        LandlordsRace race = (LandlordsRace) DataManager.findRace(game.getRaceId());
        if (race == null) return false;
        DataManager.removeGame(game.getGameId());
        if (race.getStep().equals(Race_FirstRound)) {
            logger.info(game.getGameId() + "   enter final race====================");
            dealPromotionOrWeekOut(game, race);
        } else {
            race.removeGame(game.getGameId());
            logger.info("final settle landlords!");
            DataManager.removeRace(race.getId());
            settleRace(race);
            logger.info("clean Landlord Finish！：" + race.getId());
        }
        game.cleanTimeTask();
        return true;
    }

    @Transactional
    public void settleRace(LandlordsRace race) {
        synchronized (race) {
            if (race.getStep().equals(Race_LastRound) && race.getGameIds().isEmpty()) {
                LandlordsRankUtil.rankWithRaceSettle(race.getPromotionPLayers());
                race.getPromotionPLayers().forEach(player -> {
                    int coin = race.getRaceConfig().getRankAward(player.getRank());
                    logger.info("玩家：" + player.getUser().getDisplayName() + "rank" + player.getRank() + " getCoin:" + coin);
                    EventPublisher.publish(new ModifyCoinEvent(player.getUser(), coin));
                    DataManager.removeUserRace(player.getUserId());
                    broadCastPlayerRaceResult(player, race.getRaceConfig().getRankAward(player.getRank()));
                    coinFlowService.setUserCoinFlow(player.getUserId(),coin);
                });
            }
        }
    }

    private void dealPromotionOrWeekOut(Landlords game, LandlordsRace race) {
        race.removeGame(game.getGameId());
        var players = game.getPokerPlayers();
        PokerPlayer firstPlayer = LandlordsRankUtil.getFirstRankPlayer(players);
        enterNextGameRound(firstPlayer, race);

        List<PokerPlayer> weedOutPlayers = players.stream().filter(player -> !player.equals(firstPlayer)).collect(Collectors.toList());
        LandlordsRankUtil.rankRaceWeedOutPlayers(weedOutPlayers, race);
        //weedOutPlayers.forEach(player -> DataManager.removeUserRace(player.getUserId()));
        broadCastPlayerRiseOrWeekOutPromotion(firstPlayer, false);
        weedOutPlayers.forEach(player -> {
            DataManager.removeUserRace(player.getUserId());
            logger.info(player.getUser().getDisplayName() + " out in Landlord Race！ rank is: " + player.getRank());
            race.weekOutPlayer(player);
            broadCastPlayerRiseOrWeekOutPromotion(player, true);
        });
        for (PokerPlayer player : players) {
            coinFlowService.setUserCoinFlow(player.getUserId(),race.getRaceConfig().getRaceFee());
        }
    }

    private void enterNextGameRound(PokerPlayer firstPlayer, LandlordsRace race) {
        race.enterFinalRace(firstPlayer);
        if (race.getGameIds().isEmpty() && race.getPromotionPLayers().size() == race.getRaceConfig().getGamePeopleNum()) {
            Landlords lastGame = landlordRaceProcess.matchFinalGame(race);
            ScheduledTaskUtil.schedule(() -> lastGame.readyGame(lastGame.getConfig().getReadyCountDownTime()), race.getRaceConfig().getMatchWaitTime());
        }
        UserRace userRace = DataManager.findUserRace(firstPlayer.getUserId());
        if (userRace != null) userRace.setGameId(null);
    }

    private void broadCastPlayerRiseOrWeekOutPromotion(PokerPlayer player, boolean isWeekOut) {
        Jinli.LandlordRaceRiseOrWeekOutBroadcastMessage.Builder builder =
                Jinli.LandlordRaceRiseOrWeekOutBroadcastMessage.newBuilder().setPlayer(player.toProto()).setIsWeedOut(isWeekOut);
        MessageUtil.sendMessage(player.getUserId(), buildReply(builder));
    }

    private void broadCastPlayerRaceResult(PokerPlayer player, int winAmount) {
        Jinli.LandlordRaceOverResultBroadcastMessage.Builder builder =
                Jinli.LandlordRaceOverResultBroadcastMessage.newBuilder().setPlayer(player.toProto()).setAwardCoin(winAmount);
        MessageUtil.sendMessage(player.getUserId(), buildReply(builder));
    }


    @Override
    public boolean isDisposable() {
        return false;
    }
}
