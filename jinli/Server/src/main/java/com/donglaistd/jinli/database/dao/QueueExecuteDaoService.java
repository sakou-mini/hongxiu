package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.QueueExecute;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueueExecuteDaoService {
    @Autowired
    private QueueExecuteRepository repository;

    public QueueExecute findById(String id) {
        return repository.findById(new ObjectId(id)).orElse(null);
    }

    public List<QueueExecute> findAll() {
        return repository.findAll();
    }

    public void deleteByRefIdAndTriggerType(int queueType,String refId){
        repository.deleteByRefIdAndTriggerType(refId, queueType);
    }

    public void deleteQueue(QueueExecute queueExecute){
        repository.delete(queueExecute);
    }

    public QueueExecute save(QueueExecute queueExecute){
        return repository.save(queueExecute);
    }
}
