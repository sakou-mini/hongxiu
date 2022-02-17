package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.config.TexasRaceConfig;
import com.donglaistd.jinli.database.entity.race.TexasRace;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.donglaistd.jinli.constant.GameConstant.*;

@Component
public class TexasRaceBuilder {
    private static int fee;
    private static int serviceCharge;
    private static int minPlayers;
    private static int maxPlayers;
    private static int registrationTime;
    private static int startingChips;
    private static int deadline;
    private static double firstAward;
    private static double secondAward;
    private static double thirdAward;

    @Value("${texas.race.config.fee}")
    public void setFee(int fee) {
        TexasRaceBuilder.fee = fee;
    }
    @Value("${texas.race.config.serviceCharge}")
    public void setServiceCharge(int serviceCharge) {
        TexasRaceBuilder.serviceCharge = serviceCharge;
    }

    @Value("${texas.race.config.minPlayers}")
    public void setMinPlayers(int minPlayers) {
        TexasRaceBuilder.minPlayers = minPlayers;
    }
    @Value("${texas.race.config.maxPlayers}")
    public void setMaxPlayers(int maxPlayers) {
        TexasRaceBuilder.maxPlayers = maxPlayers;
    }
    @Value("${texas.race.config.registrationTime}")
    public void setRegistrationTime(int registrationTime) {
        TexasRaceBuilder.registrationTime = registrationTime;
    }
    @Value("${texas.race.config.startingChips}")
    public void setStartingChips(int startingChips) {
        TexasRaceBuilder.startingChips = startingChips;
    }
    @Value("${texas.race.config.deadline}")
    public void setDeadline(int deadline) {
        TexasRaceBuilder.deadline = deadline;
    }
    @Value("${texas.race.config.firstAward}")
    public  void setFirstAward(double firstAward) {
        TexasRaceBuilder.firstAward = firstAward;
    }
    @Value("${texas.race.config.secondAward}")
    public  void setSecondAward(double secondAward) {
        TexasRaceBuilder.secondAward = secondAward;
    }
    @Value("${texas.race.config.thirdAward}")
    public  void setThirdAward(double thirdAward) {
        TexasRaceBuilder.thirdAward = thirdAward;
    }

    public static int getFee() {
        return fee;
    }

    public static TexasRace create() {
        return new TexasRace(buildConfig());
    }

    public static TexasRace create(TexasRaceConfig texasRaceConfig){
        return new TexasRace(texasRaceConfig);
    }

    public static TexasRaceConfig buildConfig() {
        TexasRaceConfig config = new TexasRaceConfig();
        config.setRaceFee(fee);
        config.setServiceCharge(serviceCharge);
        config.setMinPlayers(minPlayers);
        config.setMaxPlayers(maxPlayers);
        config.setStartingChips(startingChips);
        config.setRegistrationTime(registrationTime);
        config.setTitle("德州SNG赛");
        Map<Integer, Double> map = new HashMap<>();
        map.put(FIRST_RANK, firstAward);
        map.put(SECOND_RANK, secondAward);
        map.put(THIRD_RANK, thirdAward);
        config.setDeadline(deadline);
        config.setRankCoinAward(map);
        return config;
    }

    //todo ,only used on test step
    public static List<TexasRace> createTexasRacesByLevel(int maxLevel){
        List<TexasRace> texasRaces = new ArrayList<>(maxLevel);
        TexasRaceConfig texasRaceConfig;
        for (int level = 1; level <= maxLevel; level++) {
            texasRaceConfig = buildConfig();
            texasRaceConfig.upgradeRaceLevel(level);
            texasRaces.add(create(texasRaceConfig));
        }
        return texasRaces;
    }
}
