package com.donglai.blogs.service;

import com.donglai.blogs.dto.LikeCountDTO;

import java.util.List;
import java.util.Set;

/**
 * 点赞缓存操作类
 *
 * @author liang
 * @date 2020/5/15
 */
public interface BlogsLikeCacheService {

    /**
     * 获取更新过的所有点赞帖子 id
     *
     * @return
     */
    Set<Object> listUpdatedPost();

    /**
     * 测试用户是否已经对帖子点过赞
     *
     * @param userId  点赞用户 id
     * @param blogsId 待测试帖子 id
     * @return true if user has liked post
     */
    boolean hasLiked(String userId, Long blogsId);

    /**
     * 获取帖子点赞数
     *
     * @param blogsId 帖子 id
     * @return 帖子的点赞数
     */
    Integer getLikeCount(Long blogsId);

    /**
     * 点赞
     *
     * @param uid     用户 id
     * @param blogsId 帖子 id
     */
    void like(String uid, Long blogsId);

    /**
     * 取消点赞
     *
     * @param uid     用户 id
     * @param blogsId 帖子 id
     */
    void unlike(String uid, Long blogsId);

    /**
     * 某个帖子的点赞数 +1
     *
     * @param blogsId 帖子 id
     */
    void incrLikedCount(Long blogsId);

    /**
     * 某个帖子的点赞数 -1
     *
     * @param blogsId 帖子 id
     */
    void decrLikedCount(Long blogsId);

    /**
     * 删除某个帖子的点赞信息
     *
     * @param blogsId 帖子 id
     */
    void deletePostLikeSet(Long blogsId);

    /**
     * 获取帖子点赞数据列表
     */
    //List<BlogsUserLikePost> listLikedData();

    /**
     * 获取帖子点赞数列表
     */
    List<LikeCountDTO> listLikedCount();

    /**
     * 同步帖子点赞数据到数据库
     */
    void syncLikeDataToDatabase();
}
