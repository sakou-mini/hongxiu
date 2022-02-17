package com.donglai.model.db.service.live;

import com.donglai.model.db.entity.live.GiftLog;
import com.donglai.model.db.repository.live.GiftLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class GiftLogService {
    @Autowired
    private GiftLogRepository giftLogRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public GiftLog save(GiftLog giftLog) {
        Assert.notNull(giftLog, "giftLog must not be null");
        return giftLogRepository.save(giftLog);
    }

    public List<GiftLog> findBySenderId(String senderId) {
        return giftLogRepository.findBySenderId(senderId);
    }

    public List<GiftLog> saveAll(List<GiftLog> logs) {
        return giftLogRepository.saveAll(logs);
    }

    public List<GiftLog> findByReceiverUserIdAndGroupSenderIdBetweenTimes(String receiveId, int size, long startTime, long endTime) {
        Aggregation aggregation = Aggregation.newAggregation(GiftLog.class,
                Aggregation.match(Criteria.where("time").gte(startTime).lte(endTime).and("receiveId").is(receiveId)),
                Aggregation.group("$senderId")
                        .first("senderId").as("senderId").sum("sendAmount").as("sendAmount").first("receiveId").as("receiveId"),
                Aggregation.project("receiveId", "senderId", "sendAmount").andExclude("_id"),
                Aggregation.sort(Sort.Direction.DESC, "sendAmount"),
                Aggregation.limit(size)
        );
        AggregationResults<GiftLog> results = mongoTemplate.aggregate(aggregation, GiftLog.class, GiftLog.class);
        return results.getMappedResults();
    }
}
