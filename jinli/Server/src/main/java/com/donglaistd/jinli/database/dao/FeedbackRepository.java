package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeedbackRepository extends MongoRepository<Feedback,String> {

}
