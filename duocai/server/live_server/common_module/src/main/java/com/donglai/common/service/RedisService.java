package com.donglai.common.service;

import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisService {
    Set<String> keys(String keyPattern);
    /**
     * 保存属性
     */
    void set(String key, Object value, long time);

    /**
     * 保存属性
     */
    void set(String key, Object value);

    /**
     * 获取属性
     */
    Object get(String key);

    /**
     * 删除属性
     */
    Boolean del(String key);

    /**
     * 批量删除属性
     */
    Long del(List<String> keys);

    /**
     * 设置过期时间
     */
    Boolean expire(String key, long time);

    /**
     * 获取过期时间
     */
    Long getExpire(String key);

    /**
     * 判断是否有该属性
     */
    Boolean hasKey(String key);

    /**
     * 按 delta 递增
     */
    Long incr(String key, long delta);

    /**
     * 按 delta 递减
     */
    Long decr(String key, long delta);

    /**
     * 获取 Hash 结构中的属性
     */
    Object hGet(String key, String hashKey);

    /**
     * 向 Hash 结构中放入一个属性
     */
    Boolean hSet(String key, String hashKey, Object value, long time);

    /**
     * 向 Hash 结构中放入一个属性
     */
    void hSet(String key, String hashKey, Object value);

    /**
     * 直接获取整个 Hash 结构
     */
    Map<Object, Object> hGetAll(String key);

    /**
     * 直接设置整个 Hash 结构
     */
    Boolean hSetAll(String key, Map<String, Object> map, long time);

    /**
     * 直接设置整个 Hash 结构
     */
    void hSetAll(String key, Map<String, Object> map);

    /**
     * 删除 Hash 结构中的属性
     */
    void hDel(String key, Object... hashKey);

    /**
     * 判断 Hash 结构中是否有该属性
     */
    Boolean hHasKey(String key, String hashKey);

    /**
     * Hash 结构中属性递增
     */
    Long hIncr(String key, String hashKey, Long delta);

    /**
     * Hash 结构中属性递减
     */
    Long hDecr(String key, String hashKey, Long delta);

    /**
     * 获取 Set 结构
     */
    Set<Object> sMembers(String key);

    /**
     * 向 Set 结构中添加属性
     */
    Long sAdd(String key, Object... values);

    /**
     * 向 Set 结构中添加属性
     */
    Long sAdd(long time, String key, Object... values);

    /**
     * 是否为 Set 中的属性
     */
    Boolean sIsMember(String key, Object value);

    /**
     * 获取 Set 结构的长度
     */
    Long sSize(String key);

    /**
     * 删除 Set 结构中的属性
     */
    Long sRemove(String key, Object... values);

    /**
     * 获取 List 结构中的属性
     */
    List<Object> lRange(String key, long start, long end);

    /**
     * 获取 List 结构的长度
     */
    Long lSize(String key);

    /**
     * 根据索引获取 List 中的属性
     */
    Object lIndex(String key, long index);

    /**
     * 向 List 结构中添加属性
     */
    Long lPush(String key, Object value);

    /**
     * 向 List 结构中添加属性
     */
    Long lPush(String key, Object value, long time);

    /**
     * 向 List 结构中批量添加属性
     */
    Long lPushAll(String key, Object... values);

    /**
     * 向 List 结构中批量添加属性
     */
    Long lPushAll(String key, Long time, Object... values);

    /**
     * 从 List 结构中移除属性
     */
    Long lRemove(String key, long count, Object value);

    /*
    *向TreeSet中添加属性
    */
    void zAdd(String key, Object value, double score);
    /*
     *向TreeSet中增加score
     */
    void zIncreaseScore(String key, Object value, double score);

    /*
     *向TreeSet中移除属性
     */
    void zRemove(String key, Object value);

    /*
    *从ZSet根据value获得排名
    */
    Long zRank(String key,Object value);

    /*
     *从ZSet获取Score
     */
    Double getScoreInzRank(String key,Object value);

    /*
     *从ZSet获取指定区间排名信息
     */
    Set<ZSetOperations.TypedTuple<Object>> zRangeWithScore(String key, long start, long end);

    /*
     *从ZSet获取所有排名信息
     */
    Set<ZSetOperations.TypedTuple<Object>> getAllzRangeInfo(String key);

    Long zSize(String key);

}
