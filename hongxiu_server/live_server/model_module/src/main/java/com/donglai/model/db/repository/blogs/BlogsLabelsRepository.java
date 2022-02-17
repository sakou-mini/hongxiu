package com.donglai.model.db.repository.blogs;

import com.donglai.model.db.entity.blogs.BlogsLabels;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Moon
 * @date 2021-11-16 16:33
 */
public interface BlogsLabelsRepository extends MongoRepository<BlogsLabels, Integer> {

    List<BlogsLabels> findByLabelsIdIn(List<Integer> labelIds);

    List<BlogsLabels> findByLabelsId(Integer id);
}
