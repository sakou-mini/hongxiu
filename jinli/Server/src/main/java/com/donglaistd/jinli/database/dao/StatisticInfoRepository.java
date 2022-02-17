package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.statistic.StatisticInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StatisticInfoRepository extends MongoRepository<StatisticInfo, String> {
    List<StatisticInfo> findByLiveUserIdIsAndTypeIsOrderByTimeDesc(String userId, Constant.StatisticType type);

    Page<StatisticInfo>  findByLiveUserIdIsAndTypeIs(String userId, Constant.StatisticType type, Pageable pageable);
}
