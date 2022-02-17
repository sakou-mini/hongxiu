package com.donglaistd.jinli.database.dao.backoffice;

import com.donglaistd.jinli.database.entity.backoffice.BackofficeUserOperationRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BackofficeUserOperationRecordDaoService {
    @Autowired
    BackofficeUserOperationRecordRepository backofficeUserOperationRecordRepository;

    public BackofficeUserOperationRecord save(BackofficeUserOperationRecord operationRecord){
        return backofficeUserOperationRecordRepository.save(operationRecord);
    }

    public List<BackofficeUserOperationRecord> saveAll(List<BackofficeUserOperationRecord> records) {
        return backofficeUserOperationRecordRepository.saveAll(records);
    }
}
