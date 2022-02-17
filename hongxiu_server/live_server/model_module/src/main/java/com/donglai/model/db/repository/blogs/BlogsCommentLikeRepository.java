package com.donglai.model.db.repository.blogs;


import com.donglai.model.db.entity.blogs.BlogsCommentLike;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BlogsCommentLikeRepository extends MongoRepository<BlogsCommentLike, String> {
    BlogsCommentLike findByCommentIdAndUserId(long commentId, String userId);

    List<BlogsCommentLike> findByCommentId(long commentId);

    List<BlogsCommentLike> findByUserId(String userId);

    long countByBlogsId(long blogsId);

    void deleteByCommentIdAndUserId(long commentId, String userId);

    void deleteByBlogsId(long blogsId);

}
