package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.CoinFlowDaoService;
import com.donglaistd.jinli.database.dao.GiftRankDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.CoinFlow;
import com.donglaistd.jinli.database.entity.rank.GiftLog;
import com.donglaistd.jinli.database.entity.rank.RankResult;
import com.donglaistd.jinli.util.Pair;
import com.donglaistd.jinli.util.RankComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.constant.CacheNameConstant.getGiftContributeRankKey;

@Service
public class GiftRankService {
    @Autowired
    RankComponent rankComponent;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    CoinFlowDaoService coinFlowDaoService;
    @Autowired
    GiftRankDaoService giftRankDaoService;

    public boolean hasRankCache(){
        String ker1 = getGiftContributeRankKey(Constant.PlatformType.PLATFORM_JINLI);
        String ker2 = getGiftContributeRankKey(Constant.PlatformType.PLATFORM_T);
        return rankComponent.hasKey(ker1) && rankComponent.hasKey(ker2);
    }

    /*1.update user rankInfo*/
    public RankResult updateUserContributeRank(Constant.PlatformType platformType, String userId, Long score) {
        String key = getGiftContributeRankKey(platformType);
        rankComponent.add(key, userId, -score);
        long rank = rankComponent.rank(key, userId);
        return new RankResult(userId, rank + 1, score);
    }

    /*2.remove user rankInfo*/
    public void removeUserContributeRank(String userId, Constant.PlatformType platformType) {
        String key = getGiftContributeRankKey(platformType);
        rankComponent.remove(key, userId);
    }

    /*3.query user rankInfo*/
    public RankResult getUserContributeRank(String userId, Constant.PlatformType platformType) {
        String key = getGiftContributeRankKey(platformType);
        Long rank = rankComponent.rank(key, userId);
        if (rank == null) {
            return new RankResult(userId, -1, 0);
        }
        Double score = rankComponent.getScoreInRank(key, userId);
        return new RankResult(userId, rank + 1, Math.abs(score.longValue()));
    }

    /*4.query Top n rankInfo*/
    public List<RankResult> getTopRankUserInfo(int n, Constant.PlatformType platformType) {
        String key = getGiftContributeRankKey(platformType);
        Set<ZSetOperations.TypedTuple<String>> typedTuples = rankComponent.rangeWithScore(key, 0, n - 1);
        return buildRedisRankToBizDo(typedTuples, 1);
    }

    /*5.query all rankInfo*/
    public List<RankResult> getAllRankInfo(Constant.PlatformType platformType) {
        String key = getGiftContributeRankKey(platformType);
        Set<ZSetOperations.TypedTuple<String>> allRangeInfo = rankComponent.getAllRangeInfo(key);
        return buildRedisRankToBizDo(allRangeInfo, 1);
    }

    //TODO 当前为总榜，如果需要划分平台。取消注释即可
    /*6.total RankInfo*/
    public void totalContributeRankInfo() {
        //统计所有的排行信息
        /*Map<Constant.PlatformType, List<String>> platformUser = userDaoService.groupUserInfoByPlatformType();
        for (Map.Entry<Constant.PlatformType, List<String>> entry : platformUser.entrySet()) {
            List<CoinFlow> coinFlows = coinFlowDaoService.getCoinFlowByUserIds(entry.getValue());
            for (CoinFlow coinFlow : coinFlows) {
                updateUserContributeRank(entry.getKey(), coinFlow.getUserId(), coinFlow.getGiftCost());
            }
        }*/
        for (CoinFlow coinFlow : coinFlowDaoService.findAll()) {
            updateUserContributeRank(Constant.PlatformType.PLATFORM_DEFAULT, coinFlow.getUserId(), coinFlow.getGiftCost());
        }
    }

    private List<RankResult> buildRedisRankToBizDo(Set<ZSetOperations.TypedTuple<String>> typedTuples, long offSet) {
        List<RankResult> rankInfos = new ArrayList<>(typedTuples.size());
        long rank = offSet;
        for (ZSetOperations.TypedTuple<String> sub : typedTuples) {
            rankInfos.add(new RankResult(sub.getValue(), rank++, new Double(Math.abs(sub.getScore())).longValue()));
        }
        return rankInfos;
    }

    //rank total Method
    public void totalRank(Constant.RankType rankType,int size) {
        long endTime = System.currentTimeMillis();
        switch (rankType){
            case CONTRIBUTION_RANK:
                totalGiftOrContributeRank(rankType, Constant.QueryTimeType.ALL,totalContributeRankInfo(size),endTime);
                break;
            case GIFT_RANK:
                totalGiftOrContributeRank(rankType, Constant.QueryTimeType.ALL,totalGiftIncomeRank(size),endTime);
                break;
        }
    }

    private void totalGiftOrContributeRank(Constant.RankType type, Constant.QueryTimeType timeType, Map<Constant.PlatformType,List<GiftLog>> platformData, long createTime){
        List<Pair<String, Integer>> collect = new ArrayList<>();
        Map<Constant.PlatformType, List<Pair<String, Integer>>> rankInfo = new HashMap<>(0);
        switch (type){
            case CONTRIBUTION_RANK:
                rankInfo = platformData.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()
                        .stream().map(g -> new Pair<>(g.getSenderId(), g.getSendAmount())).collect(Collectors.toList())));
                break;
            case GIFT_RANK:
                rankInfo = platformData.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()
                        .stream().map(g -> new Pair<>(g.getReceiveId(), g.getSendAmount())).collect(Collectors.toList())));
                break;
        }
        if(rankInfo.isEmpty()) return;
        giftRankDaoService.updateGiftRankByRankTypeAndTimeType(type, timeType, rankInfo, createTime);
    }

    //统计全平台打赏排行榜
    private Map<Constant.PlatformType, List<GiftLog>> totalGiftIncomeRank(int size){
        Map<Constant.PlatformType, List<GiftLog>> platformGiftIncomeMap = new HashMap<>();
        List<GiftLog> giftLogs = coinFlowDaoService.getGiftIncomeRank(size).stream().filter(flow -> flow.getGiftIncome() > 0)
                .map(coinFlow -> GiftLog.newInstance("", coinFlow.getUserId(), (int) coinFlow.getGiftIncome(), "", 0))
                .collect(Collectors.toList());
        platformGiftIncomeMap.put(Constant.PlatformType.PLATFORM_DEFAULT,giftLogs);
        return platformGiftIncomeMap;
    }

    //统计各个平台打赏排行榜
    private Map<Constant.PlatformType, List<GiftLog>> totalPlatformGiftIncomeRank(int size){
        Map<Constant.PlatformType, List<GiftLog>> platformGiftIncomeMap = new HashMap<>();
        Map<Constant.PlatformType, List<String>> platformUser = userDaoService.groupUserInfoByPlatformType();
        platformUser.forEach((platform,userIds)->{
            platformGiftIncomeMap.computeIfAbsent(platform, key -> coinFlowDaoService.getCoinFlowByUserIds(userIds, size)
                    .stream().filter(flow->flow.getGiftIncome()>0)
                    .map(coinFlow -> GiftLog.newInstance("", coinFlow.getUserId(), (int) coinFlow.getGiftIncome(), "", 0))
                    .collect(Collectors.toList()));
        });
        return platformGiftIncomeMap;
    }

    //统计贡献全平台总榜
    private Map<Constant.PlatformType,List<GiftLog>> totalContributeRankInfo(int topSize) {
        if(!hasRankCache())
            totalContributeRankInfo();
        Map<Constant.PlatformType, List<GiftLog>> platformRankInfo  = new HashMap<>();
        Constant.PlatformType platformType = Constant.PlatformType.PLATFORM_DEFAULT;
        List<RankResult> ranInfo = getTopRankUserInfo(topSize,platformType);
        List<GiftLog> collect = ranInfo.stream().filter(result->result.getScore()!=0)
                .map(r -> GiftLog.newInstance(r.getUserId(), "", (int) r.getScore(), "", 0)).collect(Collectors.toList());
        platformRankInfo.put(platformType, collect);
        return platformRankInfo;
    }

    //统计各个平台贡献排行榜
    private Map<Constant.PlatformType,List<GiftLog>> totalPlatformContributeRankInfo(int topSize) {
        if(!hasRankCache())
            totalContributeRankInfo();
        Map<Constant.PlatformType, List<GiftLog>> platformRankInfo  = new HashMap<>();
        for (Constant.PlatformType platformType : Constant.PlatformType.values()) {
            if(!Objects.equals(Constant.PlatformType.PLATFORM_DEFAULT,platformType) && !Objects.equals(Constant.PlatformType.UNRECOGNIZED,platformType)){
                List<RankResult> ranInfo = getTopRankUserInfo(topSize, platformType);
                List<GiftLog> collect = ranInfo.stream().filter(result->result.getScore()!=0)
                        .map(r -> GiftLog.newInstance(r.getUserId(), "", (int) r.getScore(), "", 0)).collect(Collectors.toList());
                platformRankInfo.put(platformType, collect);
            }
        }
        return platformRankInfo;
    }
}