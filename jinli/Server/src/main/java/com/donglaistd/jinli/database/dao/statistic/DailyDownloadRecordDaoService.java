package com.donglaistd.jinli.database.dao.statistic;

import com.donglaistd.jinli.database.entity.statistic.DailyDownloadRecord;
import com.donglaistd.jinli.util.TimeUtil;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyDownloadRecordDaoService {
    @Autowired
    DailyDownloadRecordRepository dailyDownloadRecordRepository;
    @Autowired
    MongoOperations mongoOperations;
    @Autowired
    MongoTemplate mongoTemplate;

    public DailyDownloadRecord finByTime(long time){
        return dailyDownloadRecordRepository.findByTime(time);
    }

    public void addDownloadCountByToday(){
        long startTime = TimeUtil.getCurrentDayStartTime();
        UpdateResult updateResult = mongoOperations.updateFirst(new Query(Criteria.where("time").is(startTime)), new Update().inc("downloadCount", 1), DailyDownloadRecord.class);
        long modifiedCount = updateResult.getModifiedCount();
        if(modifiedCount <= 0){
            synchronized (this){
                if(dailyDownloadRecordRepository.findByTime(startTime)==null){
                    dailyDownloadRecordRepository.save(DailyDownloadRecord.newInstance(1, startTime));
                }else{
                    mongoOperations.updateFirst(new Query(Criteria.where("time").is(startTime)), new Update().inc("downloadCount", 1), DailyDownloadRecord.class);
                }
            }
        }
    }

    public List<DailyDownloadRecord> findDownLoadRecordsByTimeBetween(long startTime, long endTime){
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("time").gte(startTime).and("time").lte(endTime));
        return mongoTemplate.find(Query.query(criteria), DailyDownloadRecord.class);
    }
}
