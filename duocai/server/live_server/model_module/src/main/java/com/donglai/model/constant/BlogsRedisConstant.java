package com.donglai.model.constant;

public class BlogsRedisConstant {
    public static final String BLOGS = "blogs";
    public static final String BLOGS_ID = "blogs.id";
    public static final String BLOGS_PASSED = "blogs_passed";
    public static final String BLOG_RECOMMEND = "blog_recommend";
    public static final String BLOG_RECOMMEND_HISTORY = "blog_recommend_history";
    public static final String SEPARATOR = "::";

    //blog like
    public static final String BIZ_LIKE = "like";
    public static final String BIZ_UPDATED = "updated";
    public static final String BIZ_COUNT = "count";

    //blog common
    public static final String BLOGS_COMMENT_ID = "blogs_comment::id";
    public static final String BLOGS_COMMENT = "blogs_comment";
    public static final String BLOGS_COMMENT_REPLY = "reply";

    public static  String getUserRecommendBlogListKey(String userId){
        return BLOG_RECOMMEND + SEPARATOR + userId;
    }

    public static String getUserBlogListIndexHistoryKey(String userId){
        return BLOG_RECOMMEND_HISTORY + SEPARATOR + userId;
    }

    //更新过的博客id key
    public static String getUpdateBlogsLikeKey() {
        // blogs::like::updated ->  set:{blogsId}
        return BLOGS + SEPARATOR + BIZ_LIKE + SEPARATOR + BIZ_UPDATED;
    }

    public static String getLikeKey(Long blogsId) {
        // blogs::like::{blogsId} ->  set:{userId}
        return BLOGS + SEPARATOR + BIZ_LIKE + SEPARATOR + blogsId;
    }

    public static String getLikedCountKey() {
        // blogs::like::count ->  blogsId -> int
        return BLOGS + SEPARATOR + BIZ_LIKE + SEPARATOR + BIZ_COUNT;
    }

    //==========================blog commentLike==============================
    public static String getUpdateBlogsCommentLikeKey() {
        // blogs_comment::like::updated ->  set:{commentId}
        return BLOGS_COMMENT + SEPARATOR + BIZ_LIKE + SEPARATOR + BIZ_UPDATED;
    }

    public static String getCommentLikeKey(Long commentId) {
        // blogs_comment::like::{blogsId} ->  set:{userId}
        return BLOGS_COMMENT + SEPARATOR + BIZ_LIKE + SEPARATOR + commentId;
    }

    public static String getCommentLikeCountKey() {
        // blogs_comment::like::count ->  blogsId -> int
        return BLOGS_COMMENT + SEPARATOR + BIZ_LIKE + SEPARATOR + BIZ_COUNT;
    }

    //==========================blog comment =============================
    public static String getBlogsCommentCountKey() {
        // blogs_comment::count ->  blogsId -> int
        return BLOGS_COMMENT + SEPARATOR + BIZ_COUNT;
    }

    public static String getCommentReplyCountKey() {
        // blogs_comment::reply::count ->  commentId -> int
        return BLOGS_COMMENT + SEPARATOR + BLOGS_COMMENT_REPLY + SEPARATOR + BIZ_COUNT;
    }

    public static String getBlogsCommentKey(long blogsId) {
        // blogs_comment::{blogsId} ->  blogsId -> set:{commentId}
        return BLOGS_COMMENT + SEPARATOR + blogsId;
    }

    public static String getCommentReplyKey(long commentId) {
        // blogs_comment::reply::{commentId} ->  set:{commentId}
        return BLOGS_COMMENT + SEPARATOR + BLOGS_COMMENT_REPLY + SEPARATOR + commentId;
    }
}
