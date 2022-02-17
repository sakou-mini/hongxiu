package com.donglai.live.service.impl;

import com.donglai.common.service.RedisService;
import com.donglai.live.service.GiftRankCacheService;
import com.donglai.model.db.entity.live.CoinFlow;
import com.donglai.model.db.entity.live.GiftRank;
import com.donglai.model.db.service.live.CoinFlowService;
import com.donglai.protocol.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.donglai.live.constant.LiveRedisConstant.getGiftContributeRankKey;
import static com.donglai.live.constant.LiveRedisConstant.getGiftIncomeRankKey;

@Service
public class GiftRankCacheServiceImpl implements GiftRankCacheService {
    @Value("${gift.pay.rate}")
    private double giftPayRate;

    @Autowired
    CoinFlowService coinFlowService;
    @Autowired
    RedisService redisService;

    @Override
    public void updateGiftRank(String sender, String receiver, Long score) {
        String contributeRankKey = getGiftContributeRankKey();
        String giftIncomeRankKey = getGiftIncomeRankKey();
        boolean initContributionResult = loadDataIfNotExist(Constant.RankType.CONTRIBUTION_RANK);
        boolean initIncomeResult = loadDataIfNotExist(Constant.RankType.INCOME_RANK);
        if (!initContributionResult)
            redisService.zIncreaseScore(contributeRankKey, sender, -score);
        if (!initIncomeResult)
            redisService.zIncreaseScore(giftIncomeRankKey, receiver, -score);
    }

    public boolean loadDataIfNotExist(Constant.RankType rankType) {
        String key;
        Long size;
        switch (rankType) {
            case INCOME_RANK:
                key = getGiftIncomeRankKey();
                size = redisService.zSize(key);
                if (size == null || size <= 0) {
                    totalGiftIncomeRank();
                    return true;
                }
                break;
            case CONTRIBUTION_RANK:
                key = getGiftContributeRankKey();
                size = redisService.zSize(key);
                if (size == null || size <= 0) {
                    totalContributeRankInfo();
                    return true;
                }
                break;
        }
        return false;
    }

    private List<GiftRank.RankInfo> buildRedisRankToBizDo(Set<ZSetOperations.TypedTuple<Object>> typedTuples) {
        if (typedTuples == null || typedTuples.size() <= 0) return new ArrayList<>(0);
        List<GiftRank.RankInfo> rankInfos = new ArrayList<>(typedTuples.size());
        for (ZSetOperations.TypedTuple<Object> sub : typedTuples) {
            rankInfos.add(GiftRank.RankInfo.newInstance(Objects.requireNonNull(sub.getValue()).toString(), Objects.requireNonNull(sub.getScore()).longValue()));
        }
        return rankInfos;
    }

    @Override
    public void totalContributeRankInfo() {
        List<CoinFlow> coinFlowList = coinFlowService.findAll();
        String key = getGiftContributeRankKey();
        redisService.del(key);
        for (CoinFlow coinFlow : coinFlowList) {
            redisService.zAdd(key, coinFlow.getId(), -coinFlow.getGiftCost());
        }
    }

    @Override
    public void totalGiftIncomeRank() {
        List<CoinFlow> coinFlowList = coinFlowService.findAll();
        String key = getGiftIncomeRankKey();
        redisService.del(key);
        for (CoinFlow coinFlow : coinFlowList) {
            int giftAmount = BigDecimal.valueOf(coinFlow.getGiftIncome()).divide(BigDecimal.valueOf(giftPayRate), RoundingMode.CEILING).intValue();
            redisService.zAdd(key, coinFlow.getId(), -giftAmount);
        }
    }

    @Override
    public List<GiftRank.RankInfo> getTopNRankInfoByRankType(Constant.RankType rankType, int topN) {
        loadDataIfNotExist(rankType);
        String key = "";
        switch (rankType) {
            case INCOME_RANK:
                key = getGiftIncomeRankKey();
                break;
            case CONTRIBUTION_RANK:
                key = getGiftContributeRankKey();
                break;
        }
        return buildRedisRankToBizDo(redisService.zRangeWithScore(key, 0, topN - 1))
                .stream().filter(rankInfo -> rankInfo.getScore() != 0).collect(Collectors.toList());
    }
}
