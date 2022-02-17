package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.CoinFlowDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.CoinFlow;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.race.*;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import com.donglaistd.jinli.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class UserProcess {
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    UserOperationService userOperationService;
    @Autowired
    VerifyUtil verifyUtil;
    @Autowired
    CoinFlowDaoService coinFlowDaoService;
    @Autowired
    RoomProcess roomProcess;
    @Autowired
    TaskProcess taskProcess;
    @Autowired
    DataManager dataManager;

    public Jinli.UserInfo buildUserInfo(User user){
        var userInfo = Jinli.UserInfo.newBuilder();
        if (Objects.isNull(user)) return userInfo.build();
        Constant.UserType value = userOperationService.getUserType(user);
        if (user.getPhoneNumber() != null) userInfo.setPhoneNumber(user.getPhoneNumber());
        userInfo.setAvatarUrl(user.getAvatarUrl()).setUserId(user.getId())
                .setAccountName(user.getAccountName())
                .setLevel(user.getLevel()).setDisplayName(user.getDisplayName()).setUserType(value)
                .setGameCoin(user.getGameCoin()).setVipId(user.getVipType()).setIsTourist(user.isTourist())
                .setGoldBean(String.valueOf(user.getGoldBean())).setGiftIncome(getGiftIncomeCoin(user))
                .setPlatformType(user.getPlatformType());
        return userInfo.build();
    }

    public long getGiftIncomeCoin(User user){
        return Optional.ofNullable(coinFlowDaoService.findByUserId(user.getId())).orElse(new CoinFlow()).getGiftIncome();
    }

    public void quitRoomIfHasEnterRoom(User user){
        if(user==null) return;
        String currentRoomId = user.getCurrentRoomId();
        if(!StringUtils.isNullOrBlank(currentRoomId)) {
            Optional.ofNullable(DataManager.roomMap.get(currentRoomId)).ifPresent(r -> {
                r.removeAudience(user);
                r.removeConnectLiveCodeByUser(user);
                roomProcess.sendQuiteRoomMessage(r,user);
                taskProcess.totalWatchLiveTime(user,r);
                if(!Objects.equals(user.getLiveUserId(),r.getLiveUserId())) dataManager.removeEnterRoomRecord(user.getId());
            });
        }
    }

    public void quitRaceIfNotStart(User user){
        if(user==null) return;
        UserRace userRace = DataManager.findUserRace(user.getId());
        if(userRace == null) return;
        String raceId = userRace.getRaceId();
        RaceBase race = DataManager.findRace(userRace.getRaceId());
        long raceFee = 0;
        switch (race.getRaceType()){
            case LANDLORDS:
                LandlordsRace landlordsRace = (LandlordsRace) race;
                if(landlordsRace.quitRace(user)) raceFee =  landlordsRace.getRaceConfig().getRaceFee();
                break;
            case TEXAS:
                TexasRace texasRace = (TexasRace) race;
                if(texasRace.quitRace(user)) raceFee = texasRace.getConfig().getRaceFee() + texasRace.getConfig().getServiceCharge();
                break;
            case GOLDEN_FLOWER:
                GoldenFlowerRace goldenFlowerRace = (GoldenFlowerRace) race;
                if(goldenFlowerRace.quitRace(user)) raceFee = goldenFlowerRace.getConfig().getRaceFee();
                break;
        }
        if(raceFee > 0) EventPublisher.publish(new ModifyCoinEvent(user, raceFee));
    }
}
