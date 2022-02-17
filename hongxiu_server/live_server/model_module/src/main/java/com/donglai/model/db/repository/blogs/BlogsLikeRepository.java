package com.donglai.model.db.repository.blogs;

import com.donglai.model.db.entity.blogs.BlogsLike;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BlogsLikeRepository extends MongoRepository<BlogsLike, String> {
    List<BlogsLike> findAllByBlogsId(long blogsId);

    List<BlogsLike> findAllByUserId(String userId);

    long countAllByUserId(String userId);

    BlogsLike findByUserIdAndBlogsId(String userId, long blogId);

    long countAllByBlogsAuthor(String author);

    void deleteByUserIdAndBlogsId(String userId, long blogId);

    List<BlogsLike> findLikeBlogsByBlogsAuthor(String userId);
}
