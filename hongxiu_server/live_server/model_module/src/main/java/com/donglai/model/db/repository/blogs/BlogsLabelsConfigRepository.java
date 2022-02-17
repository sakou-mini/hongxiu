package com.donglai.model.db.repository.blogs;

import com.donglai.model.db.entity.blogs.BlogsLabelsConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BlogsLabelsConfigRepository extends MongoRepository<BlogsLabelsConfig, Integer> {
    List<BlogsLabelsConfig> findByIdIn(List<Integer> labels);


    List<BlogsLabelsConfig> deleteByIdIn(List<Integer> labels);

    BlogsLabelsConfig findByLabelName(String labelName);
}
