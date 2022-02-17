package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.rank.GiftRank;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GiftRankRepository extends MongoRepository<GiftRank,String> {
    void deleteAllByRankTypeAndTimeTypeIs(Constant.RankType rankType,  Constant.QueryTimeType timeType);
}