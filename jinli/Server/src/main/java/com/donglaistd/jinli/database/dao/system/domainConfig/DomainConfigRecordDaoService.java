package com.donglaistd.jinli.database.dao.system.domainConfig;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.system.domainConfig.DomainConfigRecord;
import com.donglaistd.jinli.http.entity.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DomainConfigRecordDaoService {
    @Autowired
    DomainConfigRecordRepository domainConfigRecordRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public List<DomainConfigRecord> findAll() {
        return domainConfigRecordRepository.findAll();
    }

    public DomainConfigRecord save(DomainConfigRecord domainConfigRecord){
        return domainConfigRecordRepository.save(domainConfigRecord);
    }

    public PageInfo<DomainConfigRecord> domainConfigRecordPageInfo(PageRequest pageRequest, Constant.PlatformType platformType){
        Query query = new Query(Criteria.where("platformType").is(platformType));
        long totalNum = mongoTemplate.count(query, DomainConfigRecord.class);
        List<DomainConfigRecord> records = mongoTemplate.find(query.with(Sort.by(Sort.Order.desc("time"))).with(pageRequest), DomainConfigRecord.class);
        return new PageInfo<>(records, totalNum);
    }
}
