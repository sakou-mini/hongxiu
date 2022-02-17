package com.donglai.model.db.service.common;

import com.donglai.model.db.entity.common.H5DomainViewRecord;
import com.donglai.model.db.repository.common.H5DomainViewRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class H5DomainViewRecordService {
    @Autowired
    H5DomainViewRecordRepository repository;

    public H5DomainViewRecord save(H5DomainViewRecord record){
        return repository.save(record);
    }

    public long totalViewNumByTime(String domainName,Long startTime,long endTime){
        return repository.countByDomainAndTimeBetween(domainName, startTime, endTime);
    }
}
