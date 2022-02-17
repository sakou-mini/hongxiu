package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.config.GoldenFlowerRaceConfig;
import com.donglaistd.jinli.config.SpringContext;
import com.donglaistd.jinli.database.entity.race.GoldenFlowerRace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GoldenFlowerRaceBuilder {
    @Autowired
    private GoldenFlowerRaceConfig config;

    public GoldenFlowerRace create(){
        return GoldenFlowerRace.newInstance(config);
    }

    public GoldenFlowerRace create(GoldenFlowerRaceConfig config){
        return GoldenFlowerRace.newInstance(config);
    }

    public List<GoldenFlowerRace> createGoldenFlowerRacesByLevel(int maxLevel){
        List<GoldenFlowerRace> goldenFlowerRaces = new ArrayList<>(maxLevel);
        GoldenFlowerRaceConfig goldenFlowerRaceConfig;
        for (int level = 1; level <= maxLevel; level++) {
            goldenFlowerRaceConfig = SpringContext.getBean(GoldenFlowerRaceConfig.class);
            goldenFlowerRaceConfig.upgradeRaceLevel(level);
            goldenFlowerRaces.add(create(goldenFlowerRaceConfig));
        }
        return goldenFlowerRaces;
    }

}
