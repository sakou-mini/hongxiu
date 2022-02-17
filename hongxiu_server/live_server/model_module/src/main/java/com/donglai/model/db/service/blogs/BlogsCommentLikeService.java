package com.donglai.model.db.service.blogs;

import com.donglai.model.db.entity.blogs.BlogsCommentLike;
import com.donglai.model.db.repository.blogs.BlogsCommentLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogsCommentLikeService {
    @Autowired
    BlogsCommentLikeRepository blogsCommentLikeRepository;

    public BlogsCommentLike save(BlogsCommentLike blogsCommentLike) {
        return blogsCommentLikeRepository.save(blogsCommentLike);
    }

    public BlogsCommentLike findByCommentIdAndUserId(long commentId, String userId) {
        return blogsCommentLikeRepository.findByCommentIdAndUserId(commentId, userId);
    }

    public List<BlogsCommentLike> findLikeCommentByCommentId(long commentId) {
        return blogsCommentLikeRepository.findByCommentId(commentId);
    }

    public List<BlogsCommentLike> findLikeCommentByUserId(String userId) {
        return blogsCommentLikeRepository.findByUserId(userId);
    }

    public long countBlogsCommentLikeNum(long blogsId) {
        return blogsCommentLikeRepository.countByBlogsId(blogsId);
    }

    public void deleteByCommentIdAndUserId(long commentId, String userId) {
        blogsCommentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }

    public void deleteByBlogsId(long blogId) {
        blogsCommentLikeRepository.deleteByBlogsId(blogId);
    }
}
