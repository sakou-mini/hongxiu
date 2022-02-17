package com.donglai.blogs.service.impl;

import com.donglai.blogs.service.UserBlogsLikeRankCacheService;
import com.donglai.common.service.RedisService;
import com.donglai.model.db.service.blogs.BlogsLikeService;
import com.donglai.model.dto.RankInfoDTO;
import com.donglai.model.dto.UserBlogsLikeNumDTO;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.donglai.blogs.constant.BlogsRedisConstant.getBlogsUserLikeRankKey;

@Component
public class UserBlogsLikeRankCacheServiceImpl implements UserBlogsLikeRankCacheService {
    final BlogsLikeService blogsLikeService;

    final RedisService redisService;

    public UserBlogsLikeRankCacheServiceImpl(RedisService redisService, BlogsLikeService blogsLikeService) {
        this.redisService = redisService;
        this.blogsLikeService = blogsLikeService;
    }

    @Override
    public List<String> listTopNBlogsLikeRank(int top) {
        String rankKey = getBlogsUserLikeRankKey();
        loadDataIfNotExist(rankKey);
        List<RankInfoDTO> rankInfoDTOS = buildRedisRankToBizDo(redisService.zRangeWithScore(rankKey, 0, top - 1));
        return rankInfoDTOS.stream().filter(rankInfo -> rankInfo.getScore() != 0).map(RankInfoDTO::getId).collect(Collectors.toList());
    }

    @Override
    public void updateUserBlogsLikeNum(String userId, Long score) {
        String rankKey = getBlogsUserLikeRankKey();
        loadDataIfNotExist(rankKey);
        redisService.zIncreaseScore(rankKey, userId, -score);
    }

    @Override
    public void syncBlogsLikeRankToDataBase() {
        //TODO .同步给数据库，(可选）
    }

    private List<RankInfoDTO> buildRedisRankToBizDo(Set<ZSetOperations.TypedTuple<Object>> typedTuples) {
        if (typedTuples == null || typedTuples.size() <= 0) return new ArrayList<>(0);
        return typedTuples.stream().map(sub -> RankInfoDTO.newInstance((Objects.requireNonNull(sub.getValue()).toString()),
                Objects.requireNonNull(sub.getScore()).longValue())).collect(Collectors.toList());

    }

    private void loadDataIfNotExist(String key) {
        Long setSize = redisService.zSize(key);
        if (setSize == null || setSize <= 0) {
            //查询所有的
            List<UserBlogsLikeNumDTO> userBlogsLikeNumDTOS = blogsLikeService.totalAllUserBlogsLikeInfo();
            for (UserBlogsLikeNumDTO userBlogsLikeNumDTO : userBlogsLikeNumDTOS) {
                //要逆序排序，因此取反
                redisService.zAdd(key, userBlogsLikeNumDTO.getUserId(), -userBlogsLikeNumDTO.getLikeNum());
            }
        }
    }
}
