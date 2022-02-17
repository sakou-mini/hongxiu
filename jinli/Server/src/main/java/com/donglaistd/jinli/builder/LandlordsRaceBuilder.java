package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.config.SpringContext;
import com.donglaistd.jinli.database.entity.race.LandlordsRace;
import com.donglaistd.jinli.config.LandlordRaceConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class LandlordsRaceBuilder {

    @Resource
    LandlordRaceConfig landlordRaceConfig;

    public LandlordsRace create(){
        LandlordsRace landlordsRace = LandlordsRace.newInstance(landlordRaceConfig);
        return landlordsRace;
    }

    public LandlordsRace create(LandlordRaceConfig config){
        return LandlordsRace.newInstance(config);
    }

    //todo ,only used on test step
    public List<LandlordsRace> createLandlordRacesLevel(int maxLevel){
        List<LandlordsRace> landlordsRaces = new ArrayList<>(maxLevel);
        LandlordRaceConfig raceConfig;
        int raceFee = landlordRaceConfig.getRaceFee();
        for (int level = 1; level <= maxLevel; level++) {
            raceConfig = SpringContext.getBean(LandlordRaceConfig.class);
            raceConfig.upgradeRaceLevel(level);
            landlordsRaces.add(create(raceConfig));
        }
        return landlordsRaces;
    }
}
