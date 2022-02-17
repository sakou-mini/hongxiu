package com.donglai.model.db.repository.live;

import com.donglai.model.db.entity.live.GiftRank;
import com.donglai.protocol.Constant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftRankRepository extends MongoRepository<GiftRank, Long> {
    void deleteAllByRankType(Constant.RankType rankType);

    GiftRank findByRankType(Constant.RankType rankType);
}