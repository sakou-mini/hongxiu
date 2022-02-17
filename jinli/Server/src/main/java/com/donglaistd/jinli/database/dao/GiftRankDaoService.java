package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.rank.GiftRank;
import com.donglaistd.jinli.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class GiftRankDaoService {
    @Autowired
    private GiftRankRepository giftRankRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    MongoOperations mongoOperations;

    @Transactional
    public GiftRank save(GiftRank giftRank) {
        return giftRankRepository.save(giftRank);
    }

    @Transactional
    public List<GiftRank> saveAll(List<GiftRank> giftRankList) {
        return giftRankRepository.saveAll(giftRankList);
    }

    public GiftRank findByRankTypeAndTimeType(Constant.RankType rankType, Constant.QueryTimeType timeType) {
        Criteria criteria = Criteria.where("rankType").is(rankType).and("timeType").is(timeType);
        Query query = Query.query(criteria).with(Sort.by("createTime").descending());
        return mongoTemplate.findOne(query, GiftRank.class);
    }

    public GiftRank updateGiftRankByRankTypeAndTimeType(Constant.RankType rankType, Constant.QueryTimeType timeType, List<Pair<String, Integer>> rankInfo, long createTime){
        giftRankRepository.deleteAllByRankTypeAndTimeTypeIs(rankType, timeType);
        return giftRankRepository.save(GiftRank.newInstance(createTime, timeType, rankType, rankInfo));
    }

    public GiftRank updateGiftRankByRankTypeAndTimeType(Constant.RankType rankType, Constant.QueryTimeType timeType, Map<Constant.PlatformType, List<Pair<String, Integer>>> rankInfo, long createTime){
        giftRankRepository.deleteAllByRankTypeAndTimeTypeIs(rankType, timeType);
        return giftRankRepository.save(GiftRank.newInstance(createTime, timeType, rankType, rankInfo));
    }
}
