package com.donglai.account.util;

import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.proto.metadata.Metadata;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MetaUtil {
    private static final Metadata.Table table = SpringApplicationContext.getBean(Metadata.Table.class);
    private static final Map<Integer, Metadata.PlayerDefine> playerMate = initPlayerMateMap();
    private static final Map<Integer, Metadata.VipDefine> vipMate = initVipMateMap();

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

    public static Metadata.VipDefine getVipDefineByPlayerLevel(int level) {
        Optional<Metadata.VipDefine> first = vipMate.values().stream().sorted(Comparator.comparing(Metadata.VipDefine::getVipIdValue).reversed())
                .filter(vipConfig -> level >= vipConfig.getUpgradeLvl()).findFirst();
        return first.get();
    }
}
