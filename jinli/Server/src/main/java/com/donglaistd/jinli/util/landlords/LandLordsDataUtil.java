package com.donglaistd.jinli.util.landlords;

import com.donglaistd.jinli.builder.LandlordsRaceBuilder;
import com.donglaistd.jinli.builder.UserBuilder;
import com.donglaistd.jinli.config.LandlordRaceConfig;
import com.donglaistd.jinli.config.SpringContext;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.landlord.PokerPlayer;
import com.donglaistd.jinli.database.entity.race.LandlordsRace;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Component
public class LandLordsDataUtil {

    @Autowired
    LandlordsRaceBuilder landlordsRaceBuilder;

    private static UserBuilder userBuilder;

    private static final Logger logger = Logger.getLogger(LandLordsDataUtil.class.getName());

    public LandlordsRace mockNewRace(LandlordRaceConfig config) {
        LandlordsRace landLordsRace = landlordsRaceBuilder.create(config);
        DataManager.addRace(landLordsRace);
        logger.info("Create new LandlordRace!"+landLordsRace.getId());
        return landLordsRace;
    }

    public static List<User> mockUser(int size){
        if(userBuilder == null)  userBuilder = SpringContext.getBean(UserBuilder.class);
        List<User> users = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            User user = userBuilder.createNoSavedUser("Account_" + System.currentTimeMillis() + i,
                    "Robot_" + StringUtils.generateRoomDisplayId(i), "", "token_test_", 1000000, "",true);
            users.add(user);
        }
        return users;
    }

    public static void mockJoinRace(LandlordsRace race){
        mockJoinRaceWithUserNum(race, race.getRaceConfig().getRobotNum());
    }

    public static void mockJoinRaceWithUserNum(LandlordsRace race,int num){
        PokerPlayer pokerPlayer;
        List<User> users = mockUser(num);
        for (User user : users) {
            pokerPlayer = PokerPlayer.newInstance(user, 0, race.getRaceConfig().getBaseCoin());
            pokerPlayer.openRobot();
            if(race.getJoinQueues().size() >= race.getRaceConfig().getJoinPeopleNum())
                return;
            race.getJoinQueues().add(pokerPlayer);
        }
    }
}
