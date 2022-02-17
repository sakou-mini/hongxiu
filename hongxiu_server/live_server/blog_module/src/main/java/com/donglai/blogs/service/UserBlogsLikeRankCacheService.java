package com.donglai.blogs.service;

import java.util.List;

/**
 * @author xiaotian
 * @date 2021/11/22
 */
public interface UserBlogsLikeRankCacheService {
    /**
     * @param top topN rank
     * @return userIds
     */
    List<String> listTopNBlogsLikeRank(int top);

    /**
     * @param userId blogs author
     * @param score  positiveNumber is addLikeNum,  negativeNumber is decLikeNum
     */
    void updateUserBlogsLikeNum(String userId, Long score);

    void syncBlogsLikeRankToDataBase();

}
