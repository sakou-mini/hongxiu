package com.donglai.blogs.service;

import com.donglai.blogs.dto.LikeCountDTO;

import java.util.List;
import java.util.Set;

/**
 * 评论点赞缓存操作类
 *
 * @author liang
 * @date 2020/5/15
 */
public interface BlogsCommentLikeCacheService {

    /**
     * 获取更新过的所有评论id
     *
     * @return
     */
    Set<Object> listUpdatedPost();

    /**
     * 测试用户是否已经对评论点过赞
     *
     * @param userId    点赞用户 id
     * @param commentId 待测试评论 id
     * @return true if user has liked post
     */
    boolean hasLiked(String userId, Long commentId);

    /**
     * 获取评论点赞数
     *
     * @param commentId 评论 id
     * @return 评论的点赞数
     */
    Integer getLikeCount(Long commentId);

    /**
     * 点赞
     *
     * @param uid       用户 id
     * @param commentId 评论 id
     */
    void like(String uid, Long commentId);

    /**
     * 取消点赞
     *
     * @param uid       用户 id
     * @param commentId 评论 id
     */
    void unlike(String uid, Long commentId);

    /**
     * 某个评论的点赞数 +1
     *
     * @param commentId 评论 id
     */
    void incrLikedCount(Long commentId);

    /**
     * 某个评论的点赞数 -1
     *
     * @param commentId 评论 id
     */
    void decrLikedCount(Long commentId);

    /**
     * 删除某个评论的点赞信息
     *
     * @param commentId 评论 id
     */
    void deletePostLikeSet(Long commentId);

    /**
     * 获取评论点赞数列表
     */
    List<LikeCountDTO> listLikedCount();

    /**
     * 同步评论点赞数据到数据库
     */
    void syncLikeDataToDatabase();
}
