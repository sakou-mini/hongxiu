package com.donglai.model.db.repository.blogs;

import com.donglai.model.db.entity.blogs.BlogsInteractive;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Moon
 * @date 2022-01-11 10:42
 */
public interface BlogsInteractiveRepository extends MongoRepository<BlogsInteractive, Long> {

}
