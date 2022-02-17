package com.donglai.model.db.repository.blogs;

import com.donglai.model.db.entity.blogs.BlogsFavorite;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BlogsFavoriteRepository extends MongoRepository<BlogsFavorite, String> {
    List<BlogsFavorite> findByUserId(String userId);

    BlogsFavorite findByBlogsIdAndUserId(long blogsId, String userId);

    long countByUserId(String userId);

}
