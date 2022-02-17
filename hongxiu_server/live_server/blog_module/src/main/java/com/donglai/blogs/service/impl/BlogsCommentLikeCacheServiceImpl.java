package com.donglai.blogs.service.impl;

import com.donglai.blogs.constant.BlogsRedisConstant;
import com.donglai.blogs.dto.LikeCountDTO;
import com.donglai.blogs.service.BlogsCommentLikeCacheService;
import com.donglai.common.service.RedisService;
import com.donglai.model.db.entity.blogs.BlogsComment;
import com.donglai.model.db.entity.blogs.BlogsCommentLike;
import com.donglai.model.db.service.blogs.BlogsCommentLikeService;
import com.donglai.model.db.service.blogs.BlogsCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BlogsCommentLikeCacheServiceImpl implements BlogsCommentLikeCacheService {
    public static final long LIKE_COUNT_DELTA = 1L;
    public static final long LIKE_EXPIRE_TIME = 3600L;
    @Autowired
    RedisService redisService;
    @Autowired
    BlogsCommentService blogsCommentService;
    @Autowired
    BlogsCommentLikeService blogsCommentLikeService;

    private void addUpdatedBlogsCommentId(Long commentId) {
        String key = BlogsRedisConstant.getUpdateBlogsCommentLikeKey();
        redisService.sAdd(key, commentId);
    }

    //从数据库加载数据点赞的信息如果不存在
    private void loadDataIfNotExist(String key, Long commentId) {
        Long size = redisService.sSize(key);
        if (size == null || size == 0) {
            List<String> userIds = blogsCommentLikeService.findLikeCommentByCommentId(commentId).stream().map(BlogsCommentLike::getUserId).collect(Collectors.toList());
            if (!userIds.isEmpty()) {
                userIds.forEach(userId -> redisService.sAdd(key, userId));
                redisService.expire(key, LIKE_EXPIRE_TIME);
                setLikeCountNum(commentId, userIds.size());
            }
        }
    }

    private void setLikeCountNum(Long commentId, Integer likeCount) {
        String likedCountKey = BlogsRedisConstant.getCommentLikeCountKey();
        redisService.hSet(likedCountKey, commentId.toString(), likeCount, LIKE_EXPIRE_TIME);
    }

    @Override
    public Set<Object> listUpdatedPost() {
        String key = BlogsRedisConstant.getUpdateBlogsCommentLikeKey();
        return redisService.sMembers(key);
    }

    @Override
    public boolean hasLiked(String userId, Long commentId) {
        String commentLikeKey = BlogsRedisConstant.getCommentLikeKey(commentId);
        this.loadDataIfNotExist(commentLikeKey, commentId);
        return redisService.sIsMember(commentLikeKey, userId);
    }

    @Override
    public Integer getLikeCount(Long commentId) {
        String commentLikeCountKey = BlogsRedisConstant.getCommentLikeCountKey();
        Object count = redisService.hGet(commentLikeCountKey, commentId.toString());
        return (count == null) ? 0 : (Integer) count;
    }

    @Override
    public void like(String uid, Long commentId) {
        String commentLikeKey = BlogsRedisConstant.getCommentLikeKey(commentId);
        this.loadDataIfNotExist(commentLikeKey, commentId);
        if (!hasLiked(uid, commentId)) {
            redisService.sAdd(commentLikeKey, uid);
            incrLikedCount(commentId);
            this.addUpdatedBlogsCommentId(commentId);
        } else {
            log.warn("You Have Liked Already");
        }
    }

    @Override
    public void unlike(String uid, Long commentId) {
        if (!hasLiked(uid, commentId)) {
            log.warn("You Have Not Liked Already");
        }
        String commentLikeKey = BlogsRedisConstant.getCommentLikeKey(commentId);
        this.loadDataIfNotExist(commentLikeKey, commentId);
        redisService.sRemove(commentLikeKey, uid);
        decrLikedCount(commentId);
        this.addUpdatedBlogsCommentId(commentId);
    }

    @Override
    public void incrLikedCount(Long commentId) {
        String commentLikeCountKey = BlogsRedisConstant.getCommentLikeCountKey();
        redisService.hIncr(commentLikeCountKey, commentId.toString(), LIKE_COUNT_DELTA);
    }

    @Override
    public void decrLikedCount(Long commentId) {
        String commentLikeCountKey = BlogsRedisConstant.getCommentLikeCountKey();
        redisService.hDecr(commentLikeCountKey, commentId.toString(), LIKE_COUNT_DELTA);
    }

    @Override
    public void deletePostLikeSet(Long commentId) {
        redisService.del(BlogsRedisConstant.getCommentLikeKey(commentId));
        redisService.hDel(BlogsRedisConstant.getCommentLikeCountKey(), commentId.toString());
        redisService.sRemove(BlogsRedisConstant.getUpdateBlogsCommentLikeKey(), commentId);
    }

    @Override
    public List<LikeCountDTO> listLikedCount() {
        List<LikeCountDTO> likeCountList = new ArrayList<>();
        Map<Object, Object> entries = redisService.hGetAll(BlogsRedisConstant.getCommentLikeCountKey());
        entries.forEach((k, v) -> {
            String key = (String) k;
            LikeCountDTO likedCountDTO = new LikeCountDTO(Long.valueOf(key), (Integer) v);
            likeCountList.add(likedCountDTO);
        });
        return likeCountList;
    }

    @Override
    public void syncLikeDataToDatabase() {
        List<LikeCountDTO> likeCountDTOS = listLikedCount();
        BlogsComment blogsComment;
        List<BlogsComment> updateBlogsComments = new ArrayList<>();
        for (LikeCountDTO likeCountDTO : likeCountDTOS) {
            blogsComment = blogsCommentService.findById(likeCountDTO.getId());
            if (Objects.isNull(blogsComment)) continue;
            blogsComment.setLikeNum(likeCountDTO.getCount());
            //blogsComment.setReplyNum(blogsCommentCacheService.getCommentReply(blogsComment.getBlogsId()).size());
            updateBlogsComments.add(blogsComment);
        }
        blogsCommentService.saveAll(updateBlogsComments);
    }
}
