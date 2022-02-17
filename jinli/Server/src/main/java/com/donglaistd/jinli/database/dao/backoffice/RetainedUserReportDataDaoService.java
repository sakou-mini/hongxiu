package com.donglaistd.jinli.database.dao.backoffice;

import com.donglaistd.jinli.database.entity.backoffice.RetainedUserReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RetainedUserReportDataDaoService {
    @Autowired
    RetainedUserReportDataRepository retainedUserReportDataRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public RetainedUserReportData save(RetainedUserReportData retainedUserReportData){return retainedUserReportDataRepository.save(retainedUserReportData);}

    public List<RetainedUserReportData> findByDate(long startTime , long endTime){
        List<Criteria> criteriaList = new ArrayList<>();
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("date").gte(startTime).lte(endTime));
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, RetainedUserReportData.class);
    }

    public RetainedUserReportData findByData(long time) {
        return retainedUserReportDataRepository.findByDate(time);
    }
}
