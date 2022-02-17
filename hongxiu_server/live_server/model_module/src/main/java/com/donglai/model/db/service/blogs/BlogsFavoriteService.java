package com.donglai.model.db.service.blogs;

import com.donglai.model.db.entity.blogs.BlogsFavorite;
import com.donglai.model.db.repository.blogs.BlogsFavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogsFavoriteService {
    @Autowired
    BlogsFavoriteRepository blogsFavoriteRepository;

    public BlogsFavorite save(BlogsFavorite blogsFavorite) {
        return blogsFavoriteRepository.save(blogsFavorite);
    }

    public BlogsFavorite findByBlogsIdAndUserId(long blogId, String userId) {
        return blogsFavoriteRepository.findByBlogsIdAndUserId(blogId, userId);
    }

    public void delete(BlogsFavorite blogsFavorite) {
        blogsFavoriteRepository.delete(blogsFavorite);
    }

    public List<BlogsFavorite> findByUserId(String userId) {
        return blogsFavoriteRepository.findByUserId(userId);
    }

    public long countBlogsFavoriteNumByUserId(String userId) {
        return blogsFavoriteRepository.countByUserId(userId);
    }
}
