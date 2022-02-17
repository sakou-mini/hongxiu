package com.donglaistd.jinli.database.dao.statistic;

import com.donglaistd.jinli.database.entity.statistic.CommonStatisticInfo;
import com.donglaistd.jinli.constant.StatisticEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommonStatisticInfoDaoService {
    @Autowired
    CommonStatisticInfoRepository commonStatisticInfoRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public CommonStatisticInfo save(CommonStatisticInfo commonStatisticInfo){
        return commonStatisticInfoRepository.save(commonStatisticInfo);
    }

    public CommonStatisticInfo findStatisticInfoByTimeAndStatisticType(long time, StatisticEnum statisticType){
        return commonStatisticInfoRepository.findByStatisticTimeAndStatisticType(time,statisticType);
    }

    public List<CommonStatisticInfo> findStatisticInfoByTimeBetweenAndStatisticType(long startTime, long endTime, StatisticEnum statisticType){
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("statisticTime").gte(startTime), Criteria.where("statisticTime").lte(endTime), Criteria.where("statisticType").is(statisticType));
        return mongoTemplate.find(Query.query(criteria), CommonStatisticInfo.class);
    }
}
