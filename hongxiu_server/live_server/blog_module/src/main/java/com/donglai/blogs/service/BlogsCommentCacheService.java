package com.donglai.blogs.service;

import com.donglai.blogs.dto.BlogsCountDTO;

import java.util.List;

/**
 * 评论缓存操作类
 *
 * @author liang
 * @date 2020/5/15
 */
public interface BlogsCommentCacheService {
    /**
     * 获取更新过的所有评论id
     *
     * @return
     */
    //Set<Object> listUpdatedPost();
    /*
     * 获取博客的评论总数
     */
    Integer getBlogsCommentCount(Long blogsId);

    /*
     * 获取评论总数的回复总数
     * */
    Integer getCommentReplyCount(Long commentId);

    /**
     * 评论博客
     *
     * @param commentId 评论id
     * @param blogsId   帖子 id
     */
    void commentBlogs(Long commentId, Long blogsId);

    /**
     * 回复某个评论
     *
     * @param replyId         回复 id
     * @param parentCommentId 父评论id
     */
    void replyComment(Long replyId, Long parentCommentId);

    /**
     * 设置某个帖子的评论数
     *
     * @param blogsId      帖子 id
     * @param commentCount 评论总数
     */
    void setBlogsCommentCount(Long blogsId, Integer commentCount);

    /**
     * 某个帖子的评论数 +1
     *
     * @param blogsId 帖子 id
     */
    void incrBlogsCommentCount(Long blogsId);

    /**
     * 某个帖子的评论数 -1
     *
     * @param blogsId 帖子 id
     */
    void decrBlogsCommentCount(Long blogsId);


    /**
     * 设置某个评论的回复数
     *
     * @param parentCommentId 父评论id
     * @param replyCount      回复数量
     */
    void setCommentReplyCount(Long parentCommentId, Integer replyCount);

    /**
     * 某个评论的回复数 +1
     *
     * @param parentCommentId 父评论id
     */
    void incrCommentReplyCount(Long parentCommentId);

    /**
     * 某个评论的评论数 -1
     *
     * @param parentCommentId 父评论id
     */
    void decrCommentReplyCount(Long parentCommentId);

    /**
     * 删除某个评论的回复信息
     *
     * @param parentCommentId 父评论id
     */
    void deleteBlogsCommentReply(Long parentCommentId);

    void deleteBlogsComment(Long blogsId, Long commentId);

    /*
     * 获取某个博客所有评论
     * */
    List<Long> getBlogsComment(Long blogsId);

    List<Long> getCommentReply(Long commentId);

    /*获取动态评论数量列表*/
    List<BlogsCountDTO> listBlogsCommentCountList();

    /**
     * 同步动态评论数据到数据库
     */
    void syncBlogsCommentDataToDatabase();
}
