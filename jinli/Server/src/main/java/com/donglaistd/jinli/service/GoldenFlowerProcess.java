package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.builder.FriedGoldenFlowerBuilder;
import com.donglaistd.jinli.config.GoldenFlowerRaceConfig;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.goldenflower.FriedGoldenFlower;
import com.donglaistd.jinli.database.entity.game.texas.RacePokerPlayer;
import com.donglaistd.jinli.database.entity.race.GoldenFlowerRace;
import com.donglaistd.jinli.database.entity.race.UserRace;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.util.ComparatorUtil;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.ScheduledTaskUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class GoldenFlowerProcess {
    @Autowired
    private FriedGoldenFlowerBuilder builder;

    public Constant.ResultCode joinRace(User user, GoldenFlowerRace goldenFlowerRace) {
        if (Objects.isNull(goldenFlowerRace)) return Constant.ResultCode.RACE_NOT_EXISTS;
        UserRace userRace = DataManager.findUserRace(user.getId());
        if (Objects.nonNull(userRace)) return Constant.ResultCode.ALREADY_JOIN_RACE;
        int fee = goldenFlowerRace.getConfig().getRaceFee();
        if (user.getGameCoin() < fee) return Constant.ResultCode.NOT_ENOUGH_GAMECOIN;
        if (!goldenFlowerRace.joinRace(user)) return Constant.ResultCode.JOIN_RACE_FAILED;
        EventPublisher.publish(new ModifyCoinEvent(user.getId(), -fee, () -> start(goldenFlowerRace)));
        return Constant.ResultCode.SUCCESS;
    }

    public synchronized void start(GoldenFlowerRace goldenFlowerRace) {
        GoldenFlowerRaceConfig config = goldenFlowerRace.getConfig();
        if (goldenFlowerRace.getSize() == config.getMaxPlayers()) {
            ScheduledTaskUtil.schedule(() -> {
                FriedGoldenFlower goldenFlower = builder.create(goldenFlowerRace.getId(), config.getGameCount(), goldenFlowerRace.getJoinQueues().size() * config.getBaseReward()
                        , config.getCost(), goldenFlowerRace.getJoinQueues().size());
                DataManager.addGame(goldenFlower);
                goldenFlowerRace.addRaceGame(goldenFlower.getGameId());
                goldenFlower.joinAll(goldenFlowerRace.getJoinQueues());
                goldenFlowerRace.clearJoinQueues();
                goldenFlowerRace.startTimer();
            }, 2000);
        }
    }

    public synchronized void createGame(GoldenFlowerRace goldenFlowerRace) {
        GoldenFlowerRaceConfig config = goldenFlowerRace.getConfig();
        int rewardAmount = goldenFlowerRace.getJoinQueues().size() * config.getBaseReward();
        FriedGoldenFlower goldenFlower = builder.create(goldenFlowerRace.getId(), config.getGameCount(), rewardAmount, config.getCost(), goldenFlowerRace.getJoinQueues().size());
        DataManager.addGame(goldenFlower);
        goldenFlowerRace.addRaceGame(goldenFlower.getGameId());
        goldenFlower.joinAll(goldenFlowerRace.getJoinQueues());
        goldenFlowerRace.clearJoinQueues();
        goldenFlowerRace.startTimer();
    }

    public void rankPokerPlayerByInitCoin(int rankIndex, List<RacePokerPlayer> outPlayers) {
        outPlayers.sort(ComparatorUtil.getRacePokerPlayerComparator());
        for (int i = 0; i < outPlayers.size(); i++) {
            outPlayers.get(i).setRank(rankIndex + i);
        }
    }
}
