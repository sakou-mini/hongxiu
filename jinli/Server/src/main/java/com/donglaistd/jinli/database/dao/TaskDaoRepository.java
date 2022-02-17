package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaskDaoRepository extends MongoRepository<Task, String> {
    List<Task> findAllByIdIn(List<String> ids);
}
