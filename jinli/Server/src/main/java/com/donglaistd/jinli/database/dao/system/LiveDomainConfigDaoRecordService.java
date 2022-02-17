package com.donglaistd.jinli.database.dao.system;

import com.donglaistd.jinli.database.dao.BackOfficeUserDaoService;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeUser;
import com.donglaistd.jinli.database.entity.system.LiveDomainConfigRecord;
import com.donglaistd.jinli.http.entity.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class LiveDomainConfigDaoRecordService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    BackOfficeUserDaoService backOfficeUserDaoService;
    @Autowired
    private LiveDomainConfigRecordRepository repository;

    public LiveDomainConfigRecord save(LiveDomainConfigRecord record){
        return repository.save(record);
    }

    public PageInfo<LiveDomainConfigRecord> domainConfigRecordPageInfo(PageRequest pageRequest){
        long totalNum = repository.count();
        Query query = new Query();
        List<LiveDomainConfigRecord> records = mongoTemplate.find(query.with(Sort.by(Sort.Order.desc("time"))).with(pageRequest), LiveDomainConfigRecord.class);
        BackOfficeUser backOfficeUser;
        for (LiveDomainConfigRecord record : records) {
            backOfficeUser = backOfficeUserDaoService.findById(record.getBackOfficeUserId());
            if(Objects.nonNull(backOfficeUser))record.setBackOfficeName(backOfficeUser.getAccountName());
        }
        return new PageInfo<>(records, totalNum);
    }



}
