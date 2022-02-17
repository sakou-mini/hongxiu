package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.texas.Texas;
import com.donglaistd.jinli.builder.TexasBuilder;
import com.donglaistd.jinli.database.entity.race.TexasRace;
import com.donglaistd.jinli.database.entity.race.UserRace;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.ScheduledTaskUtil;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TexasRaceProcess {
    TexasBuilder texasBuilder;

    public TexasRaceProcess(TexasBuilder texasBuilder) {
        this.texasBuilder = texasBuilder;
    }

    public Constant.ResultCode joinRace(User user, TexasRace texasRace) {
        if (Objects.isNull(texasRace)) return Constant.ResultCode.RACE_NOT_EXISTS;
        UserRace userRace = DataManager.findUserRace(user.getId());
        if (userRace != null) {
            return Constant.ResultCode.ALREADY_JOIN_RACE;
        }
        int fee = texasRace.getConfig().getRaceFee() + texasRace.getConfig().getServiceCharge();
        if (user.getGameCoin() < fee) return Constant.ResultCode.NOT_ENOUGH_GAMECOIN;
        if (!texasRace.joinRace(user)) return Constant.ResultCode.JOIN_RACE_FAILED;
        EventPublisher.publish(new ModifyCoinEvent(user, -fee, () -> start(texasRace)));
        return Constant.ResultCode.SUCCESS;
    }

    public synchronized void start(TexasRace texasRace) {
        if (texasRace.getSize() == texasRace.getConfig().getMaxPlayers()) {
            ScheduledTaskUtil.schedule(() -> {
                texasRace.startTimer();
                Texas texas = texasBuilder.create(texasRace.getConfig().getMaxPlayers(), texasRace.getId());
                DataManager.addGame(texas);
                texasRace.addRaceGame(texas.getGameId());
                texas.joinAll(texasRace.getJoinQueues());
                texasRace.clearJoinQueues();
            }, 2000);
        }
    }
    public synchronized void createGame(TexasRace texasRace) {
        texasRace.startTimer();
        Texas texas = texasBuilder.create(texasRace.getConfig().getMaxPlayers(), texasRace.getId());
        DataManager.addGame(texas);
        texasRace.addRaceGame(texas.getGameId());
        texas.joinAll(texasRace.getJoinQueues());
        texasRace.clearJoinQueues();
    }
}
