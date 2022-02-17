package com.donglaistd.jinli.database.dao.invite;

import com.donglaistd.jinli.database.entity.invite.WithdrawalRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WithdrawalRecordDaoService {
    @Autowired
    WithdrawalRecordRepository withdrawalRecordRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public WithdrawalRecord save(WithdrawalRecord record){
        return withdrawalRecordRepository.save(record);
    }

    public List<WithdrawalRecord> findUserLatestRecords(String userId,long size){
        Aggregation aggregation = Aggregation.newAggregation(WithdrawalRecord.class,
                Aggregation.match(Criteria.where("userId").is(userId)),
                Aggregation.sort(Sort.Direction.DESC, "time"),
                Aggregation.limit(size),
                Aggregation.project("userId","coinFlow","time").andExclude("_id")
        );
        AggregationResults<WithdrawalRecord> records = mongoTemplate.aggregate(aggregation, WithdrawalRecord.class, WithdrawalRecord.class);
        return records.getMappedResults();
    }
}
