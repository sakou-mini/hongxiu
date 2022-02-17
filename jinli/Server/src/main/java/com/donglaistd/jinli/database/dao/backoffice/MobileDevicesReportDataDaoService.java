package com.donglaistd.jinli.database.dao.backoffice;

import com.donglaistd.jinli.database.entity.backoffice.MobileDevicesReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MobileDevicesReportDataDaoService {
    @Autowired
    MobileDevicesReportDataRepository mobileDevicesReportDataRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public MobileDevicesReportData save(MobileDevicesReportData mobileDevicesReportData){return mobileDevicesReportDataRepository.save(mobileDevicesReportData);}

    public List<MobileDevicesReportData> findByDate(long startTime , long endTime){
        List<Criteria> criteriaList = new ArrayList<>();
        Criteria criteria = new Criteria();
        criteriaList.add(Criteria.where("date").gte(startTime));
        criteriaList.add(Criteria.where("date").lte(endTime));
        criteria = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, MobileDevicesReportData.class);
    }
}
