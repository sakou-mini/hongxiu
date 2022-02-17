package com.donglai.model.db.service.blogs;

import com.donglai.model.db.entity.blogs.BlogsLike;
import com.donglai.model.db.repository.blogs.BlogsLikeRepository;
import com.donglai.model.dto.UserBlogsLikeNumDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogsLikeService {
    @Autowired
    BlogsLikeRepository blogsLikeRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public BlogsLike save(BlogsLike blogsLike) {
        return blogsLikeRepository.save(blogsLike);
    }

    public BlogsLike findByUserIdAndBlogsId(String userId, long blogsId) {
        return blogsLikeRepository.findByUserIdAndBlogsId(userId, blogsId);
    }

    public List<BlogsLike> findLikeBlogsByBlogsId(long blogsId) {
        return blogsLikeRepository.findAllByBlogsId(blogsId);
    }

    public List<BlogsLike> findLikeBlogsByUserId(String userId) {
        return blogsLikeRepository.findAllByUserId(userId);
    }

    public long countLikeBlogsByUserId(String userId) {
        return blogsLikeRepository.countAllByUserId(userId);
    }

    public long countBlogsLikeByAuthor(String userId) {
        return blogsLikeRepository.countAllByBlogsAuthor(userId);
    }

    public void deleteByUserIdAndBlogsId(String userId, long blogsId) {
        blogsLikeRepository.deleteByUserIdAndBlogsId(userId, blogsId);
    }

    public List<UserBlogsLikeNumDTO> totalAllUserBlogsLikeInfo() {
        Aggregation agg = Aggregation.newAggregation(Aggregation.group("userId").first("userId").as("userId")
                .count().as("likeNum"), Aggregation.project("userId", "likeNum"));
        return mongoTemplate.aggregate(agg, BlogsLike.class, UserBlogsLikeNumDTO.class).getMappedResults();
    }

    public List<BlogsLike> deleteAllBlogsLikeByBlogsId(long blogsId) {
        List<BlogsLike> blogsLikes = findLikeBlogsByBlogsId(blogsId);
        blogsLikeRepository.deleteAll(blogsLikes);
        return blogsLikes;
    }

    public List<BlogsLike> findLikeBlogsByBlogsAuthor(String userId) {
        return blogsLikeRepository.findLikeBlogsByBlogsAuthor(userId);
    }
}
