package com.donglaistd.jinli.database.dao.invite;

import com.donglaistd.jinli.database.entity.invite.DayGroupContributionRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DayGroupContributionRecordDaoService {

    @Autowired
    private DayGroupContributionRecordRepository dayGroupContributionRecordRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public DayGroupContributionRecord save(DayGroupContributionRecord record){
        return dayGroupContributionRecordRepository.save(record);
    }

    public List<DayGroupContributionRecord> findDayAgentRecordByTimeAndUserId(String userId, long startTime, long endTime){
        Aggregation aggregation = Aggregation.newAggregation(DayGroupContributionRecord.class,
                Aggregation.match(Criteria.where("userId").is(userId).and("time").gte(startTime).lte(endTime)),
                Aggregation.sort(Sort.Direction.ASC, "time"),
                Aggregation.project("userId","firstAgentTotalBet","secondAgentTotalBet","awardCoin","time").andExclude("_id"));
        AggregationResults<DayGroupContributionRecord> result = mongoTemplate.aggregate(aggregation, DayGroupContributionRecord.class, DayGroupContributionRecord.class);
        return result.getMappedResults();
    }
}
