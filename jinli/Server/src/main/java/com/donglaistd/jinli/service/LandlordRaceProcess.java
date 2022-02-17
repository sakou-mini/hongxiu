package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.LandlordsBuilder;
import com.donglaistd.jinli.config.LandlordRaceConfig;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.landlord.Landlords;
import com.donglaistd.jinli.database.entity.game.landlord.PokerPlayer;
import com.donglaistd.jinli.database.entity.race.LandlordsRace;
import com.donglaistd.jinli.database.entity.race.UserRace;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.task.pocker.PockBaseTask;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.MessageUtil;
import com.donglaistd.jinli.util.ScheduledTaskUtil;
import com.donglaistd.jinli.util.landlords.LandLordsDataUtil;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.LandlordsStep.AddPayRate_step;
import static com.donglaistd.jinli.Constant.LandlordsStep.GrabLandlord_step;

@Component
public class LandlordRaceProcess {
    private static final Logger logger = Logger.getLogger(LandlordRaceProcess.class.getName());

    protected LandlordsBuilder landlordsBuilder;
    protected LandLordsDataUtil landLordsDataUtil;
    private LandlordRaceConfig landlordRaceConfig;

    public LandlordRaceProcess(LandlordsBuilder landlordsBuilder, LandLordsDataUtil landLordsDataUtil,LandlordRaceConfig landlordRaceConfig) {
        this.landlordsBuilder = landlordsBuilder;
        this.landLordsDataUtil = landLordsDataUtil;
        this.landlordRaceConfig =  landlordRaceConfig;
    }

    public Constant.ResultCode joinRace(User user, LandlordsRace landlordRace) {
        if (Objects.isNull(landlordRace)) {
            logger.info(user.getDisplayName()+ ".....race Is  null !");
            return Constant.ResultCode.RACE_NOT_EXISTS;
        }
        UserRace userRace = DataManager.findUserRace(user.getId());
        if (Objects.nonNull(userRace)) {
                return Constant.ResultCode.ALREADY_JOIN_RACE;
        }
        int raceFee = landlordRace.getRaceConfig().getRaceFee();
        if (user.getGameCoin() < raceFee) return Constant.ResultCode.NOT_ENOUGH_GAMECOIN;
        if (!landlordRace.joinRace(user)) {
            return Constant.ResultCode.JOIN_RACE_FAILED;
        }
        EventPublisher.publish(new ModifyCoinEvent(user, -raceFee));
        if (landlordRace.getJoinQueues().size() == landlordRace.getRaceConfig().getJoinPeopleNum()) {
            ScheduledTaskUtil.schedule(() -> matchLandlordGame(landlordRace), landlordRaceConfig.getRaceBeginDelayTime());
            LandlordsRace landlordsRace = landLordsDataUtil.mockNewRace(landlordRace.getRaceConfig());
            logger.warning("create newRace!-->" +landlordsRace.getId());
        }
        logger.warning("user "+user.getDisplayName()+"join LandlordRace" + landlordRace.getId());
        return Constant.ResultCode.SUCCESS;
    }

    public LandlordsRace joinNewRace( LandlordRaceConfig raceConfig,User user){
        logger.info(user.getDisplayName()+ ".....join newRace !");
        //raceConfig = raceConfig == null ? landlordRaceConfig : raceConfig;
        LandlordsRace landlordsRace = landLordsDataUtil.mockNewRace(landlordRaceConfig);
        joinRace(user, landlordsRace);
        return landlordsRace;
    }

    public void matchLandlordGame(LandlordsRace race) {
        ArrayList<PokerPlayer> tempQueue = Lists.newArrayList(race.getJoinQueues());
        //Collections.shuffle(tempQueue);
        Landlords landlords;
        int queueSize = tempQueue.size();
        for (int i = 0; i < queueSize / race.getRaceConfig().getGamePeopleNum(); i++) {
            landlords = matchFirstRoundGame(tempQueue, race);
            race.addGameId(landlords.getGameId());
            DataManager.addGame(landlords);
            landlords.readyGame(landlords.getConfig().getReadyCountDownTime());
        }
    }

    private Landlords matchFirstRoundGame(List<PokerPlayer> players, LandlordsRace race) {
        LandlordRaceConfig landlordRaceConfig = race.getRaceConfig();
        List<PokerPlayer> pokerPlayers = new ArrayList<>(landlordRaceConfig.getGamePeopleNum());
        for (int i = 0; i < landlordRaceConfig.getGamePeopleNum(); i++) {
            PokerPlayer player = players.remove(0);
            player.setSeatNum(i + 1);
            pokerPlayers.add(player);
        }
        Landlords landlords = landlordsBuilder.create(pokerPlayers, race.getId());
        landlords.setMaxRound(race.getRaceConfig().getFirstRaceRound());
        return landlords;
    }

    public Landlords matchFinalGame(LandlordsRace race) {
        Landlords landlords = landlordsBuilder.create(race.getPromotionPLayers(), race.getId());
        landlords.setFinalGame(true);
        landlords.setMaxRound(race.getRaceConfig().getSecondRaceRound());
        race.getGameIds().add(landlords.getGameId());
        DataManager.addGame(landlords);
        return landlords;
    }

    public static Jinli.UserLandlordsGame queryUserRaceGameInfo(Landlords game, User user) {
        Jinli.UserLandlordsGame.Builder gameBuilder = Jinli.UserLandlordsGame.newBuilder();
        List<Card> userCards = game.getUserCards(user.getId());
        gameBuilder.setGameInfo(game.toProto()).addAllUserCards(MessageUtil.getJinliCard(userCards));
        PockBaseTask countDownTask = game.getCountDownTask();
        if (countDownTask != null) {
            if (countDownTask.getPokerPlayer() != null) gameBuilder.setCurrentPlayer(countDownTask.getPokerPlayer().toProto());
            gameBuilder.setCountDownTime(countDownTask.getLeftTime());
        }
        if (game.getStep().equals(GrabLandlord_step)) gameBuilder.addAllGrabLandlordRecords(game.getGrabLandLordRecord());
        if (game.getStep().equals(AddPayRate_step)) gameBuilder.addAllRateRecords(game.getChooseRateRecords());
        return gameBuilder.build();
    }
}
