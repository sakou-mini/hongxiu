package com.donglaistd.jinli.database.dao.backoffice;

import com.donglaistd.jinli.database.entity.backoffice.NewUserReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewUserReportDataDaoService {
    @Autowired
    NewUserReportDataRepository newUserReportDataRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public NewUserReportData save(NewUserReportData newUserReportData){return newUserReportDataRepository.save(newUserReportData);}

    public List<NewUserReportData> findByDate(long startTime , long endTime){
        List<Criteria> criteriaList = new ArrayList<>();
        Criteria criteria = new Criteria();
        criteriaList.add(Criteria.where("date").gte(startTime));
        criteriaList.add(Criteria.where("date").lte(endTime));
        criteria = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, NewUserReportData.class);
    }
}
