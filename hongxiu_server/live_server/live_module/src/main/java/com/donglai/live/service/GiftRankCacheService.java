package com.donglai.live.service;

import com.donglai.model.db.entity.live.GiftRank;
import com.donglai.protocol.Constant;

import java.util.List;

public interface GiftRankCacheService {
    void updateGiftRank(String sender, String receiver, Long score);

    void totalContributeRankInfo();

    void totalGiftIncomeRank();

    List<GiftRank.RankInfo> getTopNRankInfoByRankType(Constant.RankType rankType, int topN);
}

