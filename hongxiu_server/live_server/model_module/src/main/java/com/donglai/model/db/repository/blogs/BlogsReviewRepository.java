package com.donglai.model.db.repository.blogs;

import com.donglai.model.db.entity.blogs.BlogsReview;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Moon
 * @date 2021-11-11 15:39
 */
public interface BlogsReviewRepository extends MongoRepository<BlogsReview, Long> {
}
