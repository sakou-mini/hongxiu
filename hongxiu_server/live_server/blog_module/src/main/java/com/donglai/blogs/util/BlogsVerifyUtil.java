package com.donglai.blogs.util;

import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.protocol.Constant;

import java.util.Objects;

public class BlogsVerifyUtil {

    /**
     * check Blogs is pass status
     *
     * @param blogs
     * @return true if post is exit
     */
    public static boolean isPassBlogs(Blogs blogs) {
        return Objects.nonNull(blogs) && Objects.equals(blogs.getBlogsStatus(), Constant.BlogsStatus.BLOGS_PASS);
    }
}
