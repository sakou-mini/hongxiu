package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.race.GoldenFlowerRace;
import com.donglaistd.jinli.database.entity.race.LandlordsRace;
import com.donglaistd.jinli.database.entity.race.RaceBase;
import com.donglaistd.jinli.database.entity.race.TexasRace;
import com.donglaistd.jinli.util.DataManager;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.RaceStep.Race_Open;
import static com.donglaistd.jinli.Constant.RaceType.*;

@Service
public class RaceBuilder {
    public static List<Jinli.LandlordRace> findAllLandlordRace(int maxCount){
        return DataManager.getAllRaceByRaceType(LANDLORDS).stream().map(l -> (LandlordsRace) l)
                .filter(l -> l.getStep().equals(Race_Open)).map(LandlordsRace::toProto)
                .sorted(Comparator.comparing(raceProto->raceProto.getRaceConfig().getJoinFee()))
                .collect(Collectors.toList());
    }

    public static List<Jinli.TexasRace> findAllTexasRaceRace(int maxCount){
        return DataManager.getAllRaceByRaceType(TEXAS).stream().limit(maxCount)
                .map(t -> (TexasRace) t).map(TexasRace::toProto).sorted(Comparator.comparing(Jinli.TexasRace::getRaceFee)).collect(Collectors.toList());
    }

    public static List<Jinli.GoldenFlowerRace> findAllGoldenFlowerRace(int maxCount){
        return DataManager.getAllRaceByRaceType(GOLDEN_FLOWER).stream().limit(maxCount).map(r -> (GoldenFlowerRace) r)
                .map(GoldenFlowerRace::toProto).sorted(Comparator.comparing(Jinli.GoldenFlowerRace::getRaceFee)).collect(Collectors.toList());
    }

    public static Jinli.QueryRaceListReply.Builder getRacesByType(Jinli.QueryRaceListReply.Builder reply, Constant.RaceType raceType, int maxCount){
        switch (raceType){
            case LANDLORDS:
                reply.addAllLandlordRaces(findAllLandlordRace(maxCount));
                break;
            case TEXAS:
                reply.addAllTexasRaces(findAllTexasRaceRace(maxCount));
                break;
            case GOLDEN_FLOWER:
                reply.addAllGoldenFlowerRace(findAllGoldenFlowerRace(maxCount));
                break;
            case ALL_RACE:
                DataManager.getRaceMap().forEach((k,v)->{
                    getRacesByType(reply, k, maxCount);
                });
        }
        return reply;
    }

    public static List<LandlordsRace> filterOpenLandlordRace(List<RaceBase> raceList){
        return DataManager.getAllRaceByRaceType(LANDLORDS).stream().map(l -> (LandlordsRace) l)
                .filter(l->l.getStep().equals(Race_Open)).collect(Collectors.toList());
    }

    public static LandlordsRace getNotOpenLandlordRace(){
        return DataManager.getAllRaceByRaceType(LANDLORDS).stream().map(l -> (LandlordsRace) l)
                .filter(l->l.getStep().equals(Race_Open)).findFirst().orElse(null);
    }
}
