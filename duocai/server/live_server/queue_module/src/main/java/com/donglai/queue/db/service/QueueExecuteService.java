package com.donglai.queue.db.service;

import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.queue.db.repository.QueueExecuteRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueueExecuteService {
    @Autowired
    private QueueExecuteRepository repository;

    public QueueExecute findById(String id) {
        return repository.findById(new ObjectId(id)).orElse(null);
    }

    public List<QueueExecute> findAll() {
        return repository.findAll();
    }

}
