package com.donglai.blogs.service.impl;

import com.donglai.blogs.constant.BlogsRedisConstant;
import com.donglai.blogs.dto.LikeCountDTO;
import com.donglai.blogs.service.BlogsLikeCacheService;
import com.donglai.blogs.util.BlogsVerifyUtil;
import com.donglai.common.service.RedisService;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.blogs.BlogsLike;
import com.donglai.model.db.service.blogs.BlogsLikeService;
import com.donglai.model.db.service.blogs.BlogsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BlogsLikeCacheServiceImpl implements BlogsLikeCacheService {
    @Autowired
    RedisService redisService;
    @Autowired
    BlogsLikeService blogsLikeService;
    @Autowired
    BlogsService blogsService;

    public static final long LIKE_COUNT_DELTA = 1L;
    public static final long LIKE_EXPIRE_TIME = 3600 * 3L;

    private void addUpdatedBlogsId(Long blogsId) {
        String key = BlogsRedisConstant.getUpdateBlogsLikeKey();
        redisService.sAdd(key, blogsId);
    }

    //从数据库加载数据点赞的信息如果不存在
    private void loadDataIfNotExist(String key, Long blogsId) {
        Long size = redisService.sSize(key);
        if (size == null || size == 0) {
            List<String> userIds = blogsLikeService.findLikeBlogsByBlogsId(blogsId).stream().map(BlogsLike::getUserId).collect(Collectors.toList());
            if (!userIds.isEmpty()) {
                userIds.forEach(userId -> redisService.sAdd(key, userId));
                redisService.expire(key, LIKE_EXPIRE_TIME);
                setLikeCountNum(blogsId, userIds.size());
            }
        }
    }

    private void setLikeCountNum(Long blogsId, Integer likeCount) {
        String likedCountKey = BlogsRedisConstant.getLikedCountKey();
        redisService.hSet(likedCountKey, blogsId.toString(), likeCount, LIKE_EXPIRE_TIME);
    }

    @Override
    public Set<Object> listUpdatedPost() {
        String key = BlogsRedisConstant.getUpdateBlogsLikeKey();
        return redisService.sMembers(key);
    }

    @Override
    public boolean hasLiked(String userId, Long blogsId) {
        String blogsLikeKey = BlogsRedisConstant.getLikeKey(blogsId);
        this.loadDataIfNotExist(blogsLikeKey, blogsId);
        return redisService.sIsMember(blogsLikeKey, userId);
    }

    @Override
    public Integer getLikeCount(Long blogsId) {
        String blogsLikeCountKey = BlogsRedisConstant.getLikedCountKey();
        Object count = redisService.hGet(blogsLikeCountKey, blogsId.toString());
        return (count == null) ? 0 : (Integer) count;
    }

    @Override
    public void like(String uid, Long blogsId) {
        String key = BlogsRedisConstant.getLikeKey(blogsId);
        this.loadDataIfNotExist(key, blogsId);
        if (!hasLiked(uid, blogsId)) {
            redisService.sAdd(key, uid);
            incrLikedCount(blogsId);
            this.addUpdatedBlogsId(blogsId);
        } else {
            log.warn("You Have Liked Already");
        }
    }

    @Override
    public void unlike(String uid, Long blogsId) {
        if (!hasLiked(uid, blogsId)) {
            log.warn("You Have Not Liked Already");
        }
        String likeKey = BlogsRedisConstant.getLikeKey(blogsId);
        //this.loadDataIfNotExist(likeKey, blogsId);
        redisService.sRemove(likeKey, uid);
        decrLikedCount(blogsId);
        this.addUpdatedBlogsId(blogsId);
    }

    @Override
    public void incrLikedCount(Long blogsId) {
        String likedCountKey = BlogsRedisConstant.getLikedCountKey();
        redisService.hIncr(likedCountKey, blogsId.toString(), LIKE_COUNT_DELTA);
    }

    @Override
    public void decrLikedCount(Long blogsId) {
        String likedCountKey = BlogsRedisConstant.getLikedCountKey();
        redisService.hDecr(likedCountKey, blogsId.toString(), LIKE_COUNT_DELTA);
    }

    @Override
    public void deletePostLikeSet(Long blogsId) {
        redisService.del(BlogsRedisConstant.getLikeKey(blogsId));
        redisService.hDel(BlogsRedisConstant.getLikedCountKey(), blogsId.toString());
        redisService.sRemove(BlogsRedisConstant.getUpdateBlogsLikeKey(), blogsId);
    }

    @Override
    public List<LikeCountDTO> listLikedCount() {
        List<LikeCountDTO> likeCountList = new ArrayList<>();
        Map<Object, Object> entries = redisService.hGetAll(BlogsRedisConstant.getLikedCountKey());
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
        Blogs blogs;
        List<Blogs> updateBlogs = new ArrayList<>();
        for (LikeCountDTO likeCountDTO : likeCountDTOS) {
            // 点赞数量为无关紧要操作 -- 出错采用鸵鸟策略
            blogs = blogsService.findById(likeCountDTO.getId());
            if (BlogsVerifyUtil.isPassBlogs(blogs)) {
                blogs.setLikeNum(likeCountDTO.getCount());
                updateBlogs.add(blogs);
            }
        }
        blogsService.saveAll(updateBlogs);
    }
}
