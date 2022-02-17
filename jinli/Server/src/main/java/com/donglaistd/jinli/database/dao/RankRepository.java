package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.rank.Rank;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RankRepository extends MongoRepository<Rank,String> {
     List<Rank> findAllByRankTypeIsAndTimeBetween(Constant.RankType rankType, long startTime, long endTime);
}
