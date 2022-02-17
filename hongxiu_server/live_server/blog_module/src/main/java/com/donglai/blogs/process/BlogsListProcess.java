package com.donglai.blogs.process;

import com.donglai.blogs.constant.BlogsRedisConstant;
import com.donglai.common.service.impl.RedisServiceImpl;
import com.donglai.common.util.CastUtil;
import com.donglai.common.util.RandomUtil;
import com.donglai.model.db.entity.back.RecommendVideo;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.service.back.RecommendVideoService;
import com.donglai.model.db.service.blogs.BlogsLabelsService;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.protocol.Constant;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BlogsListProcess {
    @Value("${blog.random.range.start}")
    private int blogRangeStart;
    @Value("${blog.random.range.end}")
    private int blogRangeEnd;

    @Autowired
    RecommendVideoService recommendVideoService;

    @Autowired
    BlogsService blogsService;

    @Autowired
    BlogsLabelsService blogsLabelsService;

    @Autowired
    RedisServiceImpl redisService;

    //random user blogs list
    public List<Long> randomGetUserBlogsList(String userId, List<Integer> labelsId) {
        //校验标签
        String blogUserLabelsListKey = BlogsRedisConstant.getBlogUserLabelsListKey(userId);
        Object resLabelsId = redisService.get(blogUserLabelsListKey);

        if (Objects.nonNull(resLabelsId)) {
            Set<Integer> resLabelsIds = CastUtil.cast(resLabelsId);;
            //存储视频缓存列表
            if (!Objects.equals(resLabelsIds, new HashSet<>(labelsId))) {
                resetBlogsListToUser(userId, labelsId);
                cleanUserRandomHistoryIndex(userId);
            }
        } else {
            redisService.set(blogUserLabelsListKey, new HashSet<>(labelsId),60);
        }
        String key = BlogsRedisConstant.getUserRecommendBlogListKey(userId);
        var blogIdsForObject = redisService.lRange(key, 0, -1);
        var blogIds = blogIdsForObject == null ? new ArrayList<>(0) : blogIdsForObject.stream().map(obj -> (Long) obj).collect(Collectors.toList());
        if (blogIds.isEmpty()) resetBlogsListToUser(userId, labelsId);
        //目标数量
        int blogTargetNum = RandomUtil.getRandomInt(blogRangeStart, blogRangeEnd, null);
        int lastRandomPosition = getUserRandomHistoryIndex(userId);
        int endPosition = (blogTargetNum + lastRandomPosition);
        List<Object> ids = redisService.lRange(key, lastRandomPosition, endPosition - 1);
        if (ids.isEmpty()) return new ArrayList<>();
        saveUserRandomHistoryIndex(userId, endPosition);
        if (endPosition >= redisService.lSize(key)) {
            resetBlogsListToUser(userId, labelsId);
            cleanUserRandomHistoryIndex(userId);
        }
        return ids.stream().map(obj -> (Long) obj).collect(Collectors.toList());
    }

    //TODO 优化为标签分类存储到redis
    private List<Long> resetBlogsListToUser(String userId, List<Integer> labelsId) {
        String blogListKey = BlogsRedisConstant.getUserRecommendBlogListKey(userId);
        List<Long> blogsId;
        //标签如果为null 查全部
        if (CollectionUtils.isEmpty(labelsId)) {
            blogsId = blogsService.findAllPassBlogs().stream().map(Blogs::getId).collect(Collectors.toList());
        } else {
            blogsId = blogsLabelsService.findByLabelsList(labelsId);
            blogsId = blogsService.findByIdInAndStatusIs(blogsId,Constant.BlogsStatus.BLOGS_PASS).stream().map(Blogs::getId).collect(Collectors.toList());
        }
        if (blogsId.isEmpty()) {
            log.info("blogs  is empty! no data found");
            return new ArrayList<>();
        }
        Collections.shuffle(blogsId);

        //查询推荐视频
        List<Long> recommendVideoId = recommendVideoService.findAll().stream().map(RecommendVideo::getBlogsId).collect(Collectors.toList());

        recommendVideoId = blogsService.findByIdInAndStatusIs(recommendVideoId, Constant.BlogsStatus.BLOGS_PASS).stream().map(Blogs::getId).collect(Collectors.toList());
        //添加推荐视频
        recommendVideoId.addAll(blogsId);


        redisService.del(blogListKey);
        redisService.lPushAll(blogListKey, Sets.newLinkedHashSet(recommendVideoId).toArray());
        return blogsId;
    }

    private int getUserRandomHistoryIndex(String userId) {
        String key = BlogsRedisConstant.getUserBlogListIndexHistoryKey(userId);
        Object index = redisService.get(key);
        return index == null ? 0 : (Integer) index;
    }

    private void saveUserRandomHistoryIndex(String userId, int index) {
        String key = BlogsRedisConstant.getUserBlogListIndexHistoryKey(userId);
        redisService.set(key, index);
    }

    private void cleanUserRandomHistoryIndex(String userId) {
        String key = BlogsRedisConstant.getUserBlogListIndexHistoryKey(userId);
        redisService.del(key);
    }
}
