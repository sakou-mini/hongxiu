package com.donglaistd.jinli.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;
/**
 * @author yty
 * @version 1.0
 * @description:
 */
@Component
public class RankComponent {

    private final StringRedisTemplate redisTemplate;

    public RankComponent(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /*1.Adds elements to an zSet collection*/
    public void add(String key,String value,double score){
        redisTemplate.opsForZSet().add(key, value, score);
    }

    /*modify score*/
    public void improve(String key,String value,double score){
        redisTemplate.opsForZSet().incrementScore(key, value, score);
    }

    /*2.Remove elements to an zSet collection*/
    public void remove(String key, String value) {
        redisTemplate.opsForZSet().remove(key, value);
    }

    /*3.Get the ranking in it according to Value*/
    public Long  rank(String key,String value){
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /*4.Get rank score*/
    public Double getScoreInRank(String key,String value){
        return redisTemplate.opsForZSet().score(key, value);
    }

    /*5.Query ranking information for the range */
    public Set<ZSetOperations.TypedTuple<String>> rangeWithScore(String key, long start, long end){
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    /*6.Query all current ranking information*/
    public Set<ZSetOperations.TypedTuple<String>> getAllRangeInfo(String key){
        return redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
    }

    public boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }
}
