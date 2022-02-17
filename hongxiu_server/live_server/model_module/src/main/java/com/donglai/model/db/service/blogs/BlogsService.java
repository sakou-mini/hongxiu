package com.donglai.model.db.service.blogs;

import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.repository.blogs.BlogsRepository;
import com.donglai.model.dto.UserBlogsNumDTO;
import com.donglai.model.service.impl.es.BlogsElasticsearchServiceImpl;
import com.donglai.protocol.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.donglai.common.constant.BlogsRedisConstant.BLOGS_ID;
import static com.donglai.protocol.Constant.BlogsStatus.BLOGS_PASS;


@Service
public class BlogsService {

    @Autowired
    BlogsRepository blogsRepository;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    BlogsElasticsearchServiceImpl  blogsElasticsearchService;

    @Cacheable(value = BLOGS_ID, key = "#blogId", unless = "#result == null")
    public Blogs findById(long blogId) {
        return blogsRepository.findById(blogId).orElse(null);
    }

    @CachePut(value = BLOGS_ID, key = "#result.id", unless = "#result == null")
    public Blogs save(Blogs blogs) {
        blogs = blogsRepository.save(blogs);
        blogsElasticsearchService.updateIndex(blogs);
        return blogs;
    }

    @Caching(evict = {@CacheEvict(value = BLOGS_ID, key = "#blogId")})
    public void deleteById(long blogId) {
        blogsRepository.deleteById(blogId);
        blogsElasticsearchService.deleteIndexById(String.valueOf(blogId));
    }

    public List<Blogs> findAllPassBlogs() {
        return blogsRepository.findAllByBlogsStatusIs(BLOGS_PASS);
    }

    public List<Blogs> findByBlogsTypeAndBlogsStatus(Constant.BlogsType type, Constant.BlogsStatus status) {
        return blogsRepository.findByBlogsTypeAndBlogsStatus(type, status);
    }

    public long countUserBlogs(String userId) {
        return blogsRepository.countByUserId(userId);
    }

    public long countUserBlogsNumByBlogStatus(String userId, Constant.BlogsStatus blogsStatus) {
        return blogsRepository.countByUserIdAndBlogsStatusIs(userId, blogsStatus);
    }

    public List<Blogs> findByUserIdInAndBlogsStatusIsOrderByCreateAt(Set<String> userIds, Constant.BlogsStatus status) {
        return blogsRepository.findByUserIdInAndBlogsStatusIsOrderByCreateAt(userIds, status);
    }

    /**
     * 根据某种排序进行查询
     *
     * @return 返回查询出来的视频
     */
    public List<Blogs> findBlogsByUserIdsAndSort(Set<String> userIds, Constant.SortType sort) {
        Criteria blogsRepository = new Criteria("userId").in(userIds);
        blogsRepository.and("blogsStatus").is(BLOGS_PASS);
        Query query = Query.query(blogsRepository);
        switch (sort) {
            //点赞最多
            //最新
            case SORT_TIME:
                query.with(Sort.by(Sort.Direction.DESC, "createAt"));
                break;
            //默认点赞
            default:
                query.with(Sort.by(Sort.Direction.DESC, "likeNum"));
                break;
        }
        return mongoTemplate.find(query, Blogs.class);
    }

    public List<Blogs> findByUserId(String userId) {
        return blogsRepository.findAllByUserIdOrderByCreateAtDesc(userId);
    }

    public List<Blogs> findUserPassedBlogs(String userId) {
        return blogsRepository.findAllByUserIdAndBlogsStatusIs(userId, BLOGS_PASS);
    }

    public List<Blogs> saveAll(List<Blogs> blogsList) {
        List<Blogs> blogs = blogsRepository.saveAll(blogsList);
        for (Blogs blog : blogs) {
            redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys(BLOGS_ID + "::" + blog.getId())));
            //blogsElasticsearchService.updateDocument(blog);
        }
        blogsElasticsearchService.saveIndex(blogs);
        return blogs;
    }

    public List<Blogs> findByBlogsStatusAndUserIdInOrderByCreateAtDesc(Constant.BlogsStatus status, List<String> userIds) {
        return blogsRepository.findByBlogsStatusAndUserIdInOrderByCreateAtDesc(status, userIds);
    }

    public List<String> recommendBlogsUser(List<String> filterUser, int blogsNum) {
        Criteria criteria = Criteria.where("userId").nin(filterUser).and("blogsStatus").is(BLOGS_PASS);
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.group("userId")
                        .first("userId").as("userId")
                        .count().as("blogNum"),
                Aggregation.project("userId", "blogNum"));
        List<UserBlogsNumDTO> mappedResults = mongoTemplate.aggregate(aggregation, Blogs.class, UserBlogsNumDTO.class).getMappedResults();
        return mappedResults.stream().filter(userBlogsNumDTO -> userBlogsNumDTO.getBlogNum() >= blogsNum).map(UserBlogsNumDTO::getUserId).collect(Collectors.toList());
    }

    public List<Blogs> getUserBlogsOrderByLikeNum(String userId, int page, int size) {
        Criteria criteria = Criteria.where("userId").is(userId).and("blogsStatus").is(BLOGS_PASS);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query.with(Sort.by(Sort.Direction.DESC, "likeNum")).with(pageRequest), Blogs.class);
    }

    public long countBlogsNumByMusicId(long musicId) {
        Criteria criteria = Criteria.where("blogsMusic.$id").is(musicId);
        return mongoTemplate.count(Query.query(criteria), Blogs.class);
    }


    public List<Blogs> findByIdInAndStatusIs(List<Long> ids, Constant.BlogsStatus blogsPass) {
        return blogsRepository.findByIdInAndBlogsStatusIs(ids,blogsPass);
    }
}
