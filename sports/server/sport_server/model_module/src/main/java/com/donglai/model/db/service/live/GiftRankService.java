package com.donglai.model.db.service.live;

import com.donglai.model.db.entity.live.GiftRank;
import com.donglai.protocol.Constant;
import com.donglai.model.db.repository.live.GiftRankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.donglai.common.constant.LiveRedisConstant.GIFT_RANK;


@Service
public class GiftRankService {
    @Autowired
    GiftRankRepository giftRankRepository;
    @Autowired
    GiftRankService giftRankService;

    @Cacheable(value = GIFT_RANK, key = "#rankType", unless="#result == null")
    public GiftRank findByRankTypeAndTimeType(Constant.RankType rankType) {
        return giftRankRepository.findByRankType(rankType);
    }

    @Transactional
    @CachePut(value =GIFT_RANK,key = "#result.rankType",unless="#result == null")
    public GiftRank save(GiftRank giftRank) {
        return giftRankRepository.save(giftRank);
    }

    @Transactional
    public List<GiftRank> saveAll(List<GiftRank> giftRankList) {
        List<GiftRank> result = new ArrayList<>();
        for (GiftRank giftRank : giftRankList) {
            result.add(giftRankService.save(giftRank));
        }
        return result;
    }

    public GiftRank updateGiftRankByRankTypeAndTimeType(Constant.RankType rankType, List<GiftRank.RankInfo> rankInfo){
        GiftRank giftRank = giftRankService.findByRankTypeAndTimeType(rankType);
        if(giftRank == null) {
            giftRank = GiftRank.newInstance(rankType, rankInfo);
        }else{
            giftRank.setRankInfos(rankInfo);
            giftRank.setTotalTime(System.currentTimeMillis());
        }
        return giftRankService.save(giftRank);
    }
}
