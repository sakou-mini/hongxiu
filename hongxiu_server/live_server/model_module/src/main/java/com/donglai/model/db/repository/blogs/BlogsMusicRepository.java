package com.donglai.model.db.repository.blogs;

import com.donglai.model.db.entity.blogs.BlogsMusic;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Moon
 * @date 2021-11-30 15:06
 */
public interface BlogsMusicRepository extends MongoRepository<BlogsMusic, Long> {
}
