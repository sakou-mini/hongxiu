package com.donglai.model.db.service.blogs;

import com.donglai.model.db.entity.blogs.BlogsComment;
import com.donglai.model.db.repository.blogs.BlogsCommentRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.donglai.common.constant.BlogsRedisConstant.BLOGS_COMMENT_ID;
import static com.donglai.protocol.Constant.CommentStatus.COMMENT_PASS;


@Service
public class BlogsCommentService {
    final BlogsCommentRepository repository;
    final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    BlogsCommentService blogsCommentService;
    @Autowired
    MongoTemplate mongoTemplate;

    public BlogsCommentService(BlogsCommentRepository repository, RedisTemplate<String, Object> redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }


    @CachePut(value = BLOGS_COMMENT_ID, key = "#result.id", unless = "#result == null")
    public BlogsComment save(BlogsComment blogsComment) {
        return repository.save(blogsComment);
    }

    @Cacheable(value = BLOGS_COMMENT_ID, key = "#id", unless = "#result == null")
    public BlogsComment findById(long id) {
        return repository.findById(id).orElse(null);
    }

    public List<BlogsComment> findByIds(List<Long> ids) {
        return Lists.newArrayList(repository.findAllById(ids));
    }

    @CacheEvict(value = BLOGS_COMMENT_ID, key = "#id")
    public void delete(long id) {
        repository.deleteById(id);
    }

    public List<BlogsComment> findPassRootCommentByBlogs(long blogsId) {
        return repository.findByBlogsIdAndParentCommentIdIsNullAndStatus(blogsId, COMMENT_PASS);
    }

    public List<BlogsComment> findRootCommentByFromUser(String fromUserId, long blogsId) {
        Criteria criteria = Criteria.where("formUser").is(fromUserId).and("parentCommentId").exists(false).and("blogsId").is(blogsId).and("status").is(COMMENT_PASS);
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC, "commentTime"));
        return mongoTemplate.find(query, BlogsComment.class);
    }

    public List<BlogsComment> findPassCommentReplyByParentCommentId(long commentId) {
        return repository.findByParentCommentIdAndStatus(commentId, COMMENT_PASS);
    }

    public void saveAll(List<BlogsComment> updateBlogsComments) {
        List<BlogsComment> blogsComments = repository.saveAll(updateBlogsComments);
        cleanCacheByBlogsCommentList(blogsComments);
    }


    public long countBlogsAllCommentNum(long blogId) {
        return repository.countByBlogsId(blogId);
    }

    public List<BlogsComment> findPassedPageOfCommentByList(List<Long> blogsComment, PageRequest pageRequest) {
        return repository.findByIdInAndStatusIsOrderByLikeNumDesc(blogsComment, COMMENT_PASS, pageRequest).getContent();
    }

    public List<BlogsComment> deleteBlogsAllComment(long blogId) {
        List<BlogsComment> blogsComments = repository.findByBlogsId(blogId);
        repository.deleteAll(blogsComments);
        cleanCacheByBlogsCommentList(blogsComments);
        return blogsComments;
    }

    public void cleanCacheByBlogsCommentList(List<BlogsComment> blogsComments) {
        for (BlogsComment blogsComment : blogsComments) {
            redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys(BLOGS_COMMENT_ID + "::" + blogsComment.getId())));
        }
    }

    public long findComentCountByUserId(String userId) {
        List<BlogsComment> byFromUserIs = repository.findByFromUserIs(userId);
        return byFromUserIs.size();
    }

    public boolean isPass(Long id) {
        BlogsComment blogsComment = blogsCommentService.findById(id);
        return Objects.nonNull(blogsComment) && Objects.equals(COMMENT_PASS, blogsComment.getStatus());
    }
}
