package com.donglai.common.constant;

public class BlogsRedisConstant {
    public static final String SEPARATOR = "::";
    public static final String BLOGS = "blogs";
    public static final String BLOGS_ID = "blogs.id";

    //blog like
    public static final String BIZ_LIKE = "like";
    public static final String BIZ_COUNT = "count";

    //blog common
    public static final String BLOGS_COMMENT_ID = "blogs_comment::id";

    //blogs labels
    public static final String BLOGS_LABELS_ALL = "blogs_labels_all";

    //blogs music
    public static final String MUSIC_ID = "music_id";

    public static String getMusicIdKey(long musicId) {
        //music_id::{id}
        return MUSIC_ID + SEPARATOR + musicId;
    }
}
