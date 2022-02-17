package com.donglaistd.jinli.database.dao.statistic.record;

import com.donglaistd.jinli.database.entity.statistic.record.ConnectedLiveRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConnectedLiveRecordDaoService {
    @Autowired
    ConnectedLiveRecordRepository connectedLiveRecordRepository;

    public ConnectedLiveRecord save(ConnectedLiveRecord record){
        return connectedLiveRecordRepository.save(record);
    }

}
