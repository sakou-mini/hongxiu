package com.donglai.blogs.service.impl;

import com.donglai.blogs.constant.BlogsRedisConstant;
import com.donglai.blogs.dto.BlogsCountDTO;
import com.donglai.blogs.service.BlogsCommentCacheService;
import com.donglai.blogs.util.BlogsVerifyUtil;
import com.donglai.common.service.RedisService;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.blogs.BlogsComment;
import com.donglai.model.db.service.blogs.BlogsCommentLikeService;
import com.donglai.model.db.service.blogs.BlogsCommentService;
import com.donglai.model.db.service.blogs.BlogsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BlogsCommentCacheServiceImpl implements BlogsCommentCacheService {
    public static final long COUNT_COUNT_DELTA = 1L;
    public static final long COUNT_EXPIRE_TIME = 3600 * 3L;
    final RedisService redisService;
    final BlogsCommentService blogsCommentService;
    final BlogsCommentLikeService blogsCommentLikeService;
    @Autowired
    BlogsService blogsService;

    private void addUpdatedBlogsId(Long blogsId) {
        String key = BlogsRedisConstant.getUpdateBlogsCommentKey();
        redisService.sAdd(key, blogsId);
    }

    public BlogsCommentCacheServiceImpl(RedisService redisService, BlogsCommentService blogsCommentService, BlogsCommentLikeService blogsCommentLikeService) {
        this.redisService = redisService;
        this.blogsCommentService = blogsCommentService;
        this.blogsCommentLikeService = blogsCommentLikeService;
    }

    /*加载博客根评论数据*/
    public boolean loadBlogsCommentDataIfNotExit(String key, long blogsId) {
        Long size = redisService.sSize(key);
        if (size == null || size == 0) {
            List<Long> rootCommentIds = blogsCommentService.findPassRootCommentByBlogs(blogsId).stream().map(BlogsComment::getId).collect(Collectors.toList());
            if (!rootCommentIds.isEmpty()) {
                rootCommentIds.forEach(commentId -> redisService.sAdd(key, commentId));
                redisService.expire(key, COUNT_EXPIRE_TIME);
                setBlogsCommentCount(blogsId, rootCommentIds.size());
                return true;
            }
        }
        return false;
    }

    /*加载评论回复数据*/
    public boolean loadCommentReplyDataIfNotExit(String key, long parentCommentId) {
        Long size = redisService.sSize(key);
        if (size == null || size == 0) {
            List<Long> replyCommentIds = blogsCommentService.findPassCommentReplyByParentCommentId(parentCommentId).stream().map(BlogsComment::getId).collect(Collectors.toList());
            if (!replyCommentIds.isEmpty()) {
                replyCommentIds.forEach(replyCommentId -> redisService.sAdd(key, replyCommentId));
                redisService.expire(key, COUNT_EXPIRE_TIME);
                setCommentReplyCount(parentCommentId, replyCommentIds.size());
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer getBlogsCommentCount(Long blogsId) {
        String blogsCommentCountKey = BlogsRedisConstant.getBlogsCommentCountKey();
        Object count = redisService.hGet(blogsCommentCountKey, blogsId.toString());
        return (count == null) ? 0 : (Integer) count;
    }

    @Override
    public Integer getCommentReplyCount(Long commentId) {
        String commentReplyCountKey = BlogsRedisConstant.getCommentReplyCountKey();
        Object count = redisService.hGet(commentReplyCountKey, commentId.toString());
        return (count == null) ? 0 : (Integer) count;
    }

    @Override
    public void commentBlogs(Long commentId, Long blogsId) {
        //增加博客评论id 、评论数量 + 1
        String blogsCommentKey = BlogsRedisConstant.getBlogsCommentKey(blogsId);
        boolean initResult = this.loadBlogsCommentDataIfNotExit(blogsCommentKey, blogsId);
        if (!initResult) {
            redisService.sAdd(blogsCommentKey, commentId);
            this.incrBlogsCommentCount(blogsId);
        }
        addUpdatedBlogsId(blogsId);
    }

    @Override
    public void replyComment(Long replyId, Long parentCommentId) {
        String commentReplyKey = BlogsRedisConstant.getCommentReplyKey(parentCommentId);
        boolean initResult = this.loadCommentReplyDataIfNotExit(commentReplyKey, parentCommentId);
        if (!initResult) {
            redisService.sAdd(commentReplyKey, replyId);
            this.incrCommentReplyCount(parentCommentId);
        }
    }

    @Override
    public void setBlogsCommentCount(Long blogsId, Integer commentCount) {
        String blogsCommentCountKey = BlogsRedisConstant.getBlogsCommentCountKey();
        redisService.hSet(blogsCommentCountKey, blogsId.toString(), commentCount, COUNT_EXPIRE_TIME);
    }

    @Override
    public void incrBlogsCommentCount(Long blogsId) {
        String commentCountKey = BlogsRedisConstant.getBlogsCommentCountKey();
        redisService.hIncr(commentCountKey, blogsId.toString(), COUNT_COUNT_DELTA);
    }

    @Override
    public void decrBlogsCommentCount(Long blogsId) {
        String commentCountKey = BlogsRedisConstant.getBlogsCommentCountKey();
        redisService.hDecr(commentCountKey, blogsId.toString(), COUNT_COUNT_DELTA);
    }

    @Override
    public void setCommentReplyCount(Long parentCommentId, Integer replyCount) {
        String commentReplyCountKey = BlogsRedisConstant.getCommentReplyCountKey();
        redisService.hSet(commentReplyCountKey, parentCommentId.toString(), replyCount);
    }

    @Override
    public void incrCommentReplyCount(Long parentCommentId) {
        String commentReplyCountKey = BlogsRedisConstant.getCommentReplyCountKey();
        redisService.hIncr(commentReplyCountKey, parentCommentId.toString(), COUNT_COUNT_DELTA);
    }

    @Override
    public void decrCommentReplyCount(Long parentCommentId) {
        String commentReplyCountKey = BlogsRedisConstant.getCommentReplyCountKey();
        redisService.hDecr(commentReplyCountKey, parentCommentId.toString(), COUNT_COUNT_DELTA);
    }

    @Override
    public void deleteBlogsCommentReply(Long parentCommentId) {
        redisService.del(BlogsRedisConstant.getCommentReplyKey(parentCommentId));
        redisService.hDel(BlogsRedisConstant.getCommentReplyCountKey(), parentCommentId.toString());
    }

    @Override
    public void deleteBlogsComment(Long blogsId, Long commentId) {
        //删除动态根评论 和 修改根评论数量
        redisService.sRemove(BlogsRedisConstant.getBlogsCommentKey(blogsId), commentId);
        redisService.hDel(BlogsRedisConstant.getBlogsCommentCountKey(), blogsId.toString());
        //删除根评论下的所有回复 和 回复数量
        deleteBlogsCommentReply(commentId);
        //删除动态评论的更新记录
        redisService.sRemove(BlogsRedisConstant.getUpdateBlogsCommentKey(), blogsId);
    }

    @Override
    public List<Long> getBlogsComment(Long blogsId) {
        String blogsCommentKey = BlogsRedisConstant.getBlogsCommentKey(blogsId);
        this.loadBlogsCommentDataIfNotExit(blogsCommentKey, blogsId);
        Set<Object> commentsIds = redisService.sMembers(blogsCommentKey);
        return Objects.isNull(commentsIds) ? new ArrayList<>() : commentsIds.stream().map(id -> (Long) id).collect(Collectors.toList());
    }

    @Override
    public List<Long> getCommentReply(Long commentId) {
        String commentReplyKey = BlogsRedisConstant.getCommentReplyKey(commentId);
        loadCommentReplyDataIfNotExit(commentReplyKey, commentId);
        Set<Object> commentReplyIds = redisService.sMembers(commentReplyKey);
        return Objects.isNull(commentReplyIds) ? new ArrayList<>() : commentReplyIds.stream().map(id -> (Long) id).collect(Collectors.toList());
    }

    @Override
    public List<BlogsCountDTO> listBlogsCommentCountList() {
        String key = BlogsRedisConstant.getBlogsCommentCountKey();
        List<BlogsCountDTO> blogsCountList = new ArrayList<>();
        Map<Object, Object> entries = redisService.hGetAll(key);
        entries.forEach((k, v) -> blogsCountList.add(new BlogsCountDTO(Long.valueOf((String) k), (Integer) v)));
        return blogsCountList;
    }

    @Override
    public void syncBlogsCommentDataToDatabase() {
        List<BlogsCountDTO> blogsCountDTOS = listBlogsCommentCountList();
        List<Blogs> updateBlogs = new ArrayList<>(blogsCountDTOS.size());
        Blogs blogs;
        for (BlogsCountDTO blogsCountDTO : blogsCountDTOS) {
            blogs = blogsService.findById(blogsCountDTO.getId());
            if (BlogsVerifyUtil.isPassBlogs(blogs)) {
                blogs.setCommentNum(blogsCountDTO.getCount());
                updateBlogs.add(blogs);
            }
        }
        blogsService.saveAll(updateBlogs);
    }
}
