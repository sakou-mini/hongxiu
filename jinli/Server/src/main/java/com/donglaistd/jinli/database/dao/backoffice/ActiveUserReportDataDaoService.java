package com.donglaistd.jinli.database.dao.backoffice;

import com.donglaistd.jinli.database.entity.backoffice.ActiveUserReportData;
import com.donglaistd.jinli.database.entity.backoffice.Guess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActiveUserReportDataDaoService {
    @Autowired
    ActiveUserReportDataRepository activeUserReportDataRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public ActiveUserReportData save(ActiveUserReportData activeUserReportData){return activeUserReportDataRepository.save(activeUserReportData);}

    public List<ActiveUserReportData> findByDate(long startTime ,long endTime){
        List<Criteria> criteriaList = new ArrayList<>();
        Criteria criteria = new Criteria();
        criteriaList.add(Criteria.where("date").gte(startTime));
        criteriaList.add(Criteria.where("date").lte(endTime));
        criteria = criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, ActiveUserReportData.class);
    }
}
