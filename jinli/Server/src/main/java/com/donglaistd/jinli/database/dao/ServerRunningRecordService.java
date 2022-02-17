package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.ServerRunningRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServerRunningRecordService {
    private final ServerRunningRecordRepository serverRunningRecordRepository;
    private final MongoTemplate mongoTemplate;

    public ServerRunningRecordService(MongoTemplate mongoTemplate, ServerRunningRecordRepository serverRunningRecordRepository) {
        this.mongoTemplate = mongoTemplate;
        this.serverRunningRecordRepository = serverRunningRecordRepository;
    }

    public ServerRunningRecord save(ServerRunningRecord serverRunningRecord){
       return serverRunningRecordRepository.save(serverRunningRecord);
    }

    public ServerRunningRecord findNearestLastServerRunningRecord(){
        var agg = Aggregation.newAggregation(Aggregation.limit(1), Aggregation.sort(Sort.Direction.DESC, "recordTime"));
        var result = mongoTemplate.aggregate(agg, ServerRunningRecord.class, ServerRunningRecord.class);
        List<ServerRunningRecord> records = result.getMappedResults();
        if(!records.isEmpty()) return records.get(0);
        return null;
    }
}
