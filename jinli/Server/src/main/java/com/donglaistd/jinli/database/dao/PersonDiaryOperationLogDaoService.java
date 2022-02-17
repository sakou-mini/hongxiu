package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.backoffice.PersonDiaryOperationlog;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PersonDiaryOperationLogDaoService {
    @Autowired
    PersonDoaryOperationLogRepository logRepository;

    public PersonDiaryOperationlog findById(ObjectId id){
        return logRepository.findById(id);
    }

    public List<PersonDiaryOperationlog> findByIsApproval(boolean isApproval){
        return logRepository.findByIsApproval(isApproval);
    }

    public void save(PersonDiaryOperationlog PersonDiaryOperationlog){
        logRepository.save(PersonDiaryOperationlog);
    }

    public PersonDiaryOperationlog findByPersonDiaryId(String id){
        return logRepository.findByPersonDiaryId(id);
    }

    public PersonDiaryOperationlog findByPersonDiaryIdAndIsApproval(String id, boolean isApproval){
        return logRepository.findByPersonDiaryIdAndIsApproval(id,isApproval);
    }
}
