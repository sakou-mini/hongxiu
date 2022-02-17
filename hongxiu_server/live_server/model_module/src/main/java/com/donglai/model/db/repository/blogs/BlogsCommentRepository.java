package com.donglai.model.db.repository.blogs;

import com.donglai.model.db.entity.blogs.BlogsComment;
import com.donglai.protocol.Constant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BlogsCommentRepository extends MongoRepository<BlogsComment, Long> {
    List<BlogsComment> findByBlogsIdAndParentCommentIdIsNullAndStatus(long blogsId, Constant.CommentStatus status);

    //List<BlogsComment> findByFromUserAndParentCommentIdIsNullAndBlogsIdOrderByCommentTimeDesc(String userId, long blogsId);

    List<BlogsComment> findByParentCommentIdAndStatus(long parentCommentId, Constant.CommentStatus status);

    long countByBlogsId(long blogsId);


    /**
     * 查询所有
     *
     * @param id 评论ID
     * @return 返回评论对象
     */
    Page<BlogsComment> findByIdInAndStatusIsOrderByLikeNumDesc(List<Long> id,  Constant.CommentStatus status, Pageable pageable);

    List<BlogsComment> findByBlogsId(long blogsId);

    List<BlogsComment> findByFromUserIs(String userId);
}
