package com.donglai.model.util;

import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.blogs.BlogsComment;
import com.donglai.protocol.Constant;

import java.util.Comparator;

public class ComparatorUtil {

    public static Comparator<BlogsComment> getBlogsCommentComparator() {
        return (comment1, comment2) -> {
            long sortNum1 = comment1.getLikeNum();
            long sortNum2 = comment2.getLikeNum();
            return (int) (sortNum2 - sortNum1);
        };
    }

    public static Comparator<Blogs> getBlogsComparatorBySortType(Constant.SortType sortType) {
        return (blogs1, blogs2) -> {
            switch (sortType) {
                case SORT_LIKENUM:
                    return (int) (blogs2.getLikeNum() - blogs1.getLikeNum());
                case SORT_COMMENT:
                    return (int) (blogs2.getCommentNum() - blogs1.getCommentNum());
                default:
                    return (int) (blogs2.getCreateAt() - blogs1.getCreateAt());
            }
        };
    }
}
