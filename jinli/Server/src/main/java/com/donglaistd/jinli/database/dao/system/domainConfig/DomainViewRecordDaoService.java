package com.donglaistd.jinli.database.dao.system.domainConfig;

import com.donglaistd.jinli.database.entity.system.domainConfig.DomainViewRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainViewRecordDaoService {
    @Autowired
    DomainViewRecordRepository domainViewRecordRepository;

    public long totalViewNumByTime(String domainName,Long startTime,long endTime){
        return domainViewRecordRepository.countByDomainAndTimeBetween(domainName, startTime, endTime);
    }

    public DomainViewRecord save(DomainViewRecord domainViewRecord) {
        return domainViewRecordRepository.save(domainViewRecord);
    }
}
