package com.donglaistd.jinli.util;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.SpringContext;
import com.donglaistd.jinli.metadata.Metadata;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MetaUtil {
    private static final Metadata.Table table = SpringContext.getBean(Metadata.Table.class);
    private static final Map<Integer, Metadata.PlayerDefine> playerMate = initPlayerMateMap();
    private static final Map<Integer, Metadata.VipDefine> vipMate = initVipMateMap();
    private static final Map<Integer, Metadata.IncreaseBetDefine> increaseBetMap = initIncreaseBetMap();

    private static Map<Integer, Metadata.PlayerDefine> initPlayerMateMap() {
        return table.getPlayerList().stream().collect(Collectors.toMap(Metadata.PlayerDefine::getCurLvl, Function.identity()));
    }

    private static Map<Integer, Metadata.VipDefine> initVipMateMap() {
        return table.getVipList().stream().collect(Collectors.toMap(Metadata.VipDefine::getVipIdValue, Function.identity()));
    }

    public static Metadata.PlayerDefine getPlayerDefineByCurrentLevel(int level) {
        return playerMate.get(level);
    }

    public static Metadata.VipDefine getVipDefineByCurrentLevel(int level) {
        return vipMate.getOrDefault(level, null);
    }

    public static Map<Integer, Metadata.IncreaseBetDefine> initIncreaseBetMap() {
        return table.getIncreaseBetList().stream().collect(Collectors.toMap(Metadata.IncreaseBetDefine::getLevel, Function.identity()));
    }
    public static Metadata.IncreaseBetDefine getIncreaseBetDefineByLevel(int level) {
        return increaseBetMap.get(level);
    }

    public static List<Jinli.StructureTable> buildStructureTable() {
        return increaseBetMap.values().stream()
                .map(d -> Jinli.StructureTable.newBuilder().setLevel(d.getLevel()).setBet(d.getBet()).setPreBet(d.getPreBet()).setTime(d.getTime()).build())
                .sorted(Comparator.comparing(Jinli.StructureTable::getLevel)).collect(Collectors.toList());
    }

    public static Metadata.VipDefine getVipDefineByPlayerLevel(int level){
        Optional<Metadata.VipDefine> first = vipMate.values().stream().sorted(Comparator.comparing(Metadata.VipDefine::getVipIdValue).reversed())
                .filter(vipConfig -> level >= vipConfig.getUpgradeLvl()).findFirst();
        return first.get();
    }
}
