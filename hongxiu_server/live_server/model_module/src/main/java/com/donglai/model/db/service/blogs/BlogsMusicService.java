package com.donglai.model.db.service.blogs;

import com.donglai.common.constant.BlogsRedisConstant;
import com.donglai.common.service.RedisService;
import com.donglai.model.db.entity.blogs.BlogsMusic;
import com.donglai.model.db.repository.blogs.BlogsMusicRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.donglai.common.constant.BlogsRedisConstant.MUSIC_ID;

/**
 * @author Moon
 * @date 2021-11-30 15:47
 */
@Service
public class BlogsMusicService {
    @Autowired
    private BlogsMusicRepository repository;
    @Autowired
    BlogsService blogsService;
    @Autowired
    RedisService redisService;

    @CachePut(value = MUSIC_ID, key = "#blogsMusic.id", unless = "#result == null")
    public BlogsMusic save(BlogsMusic blogsMusic) {
        return repository.save(blogsMusic);
    }

    @Cacheable(value = MUSIC_ID, key = "#id", unless = "#result == null")
    public BlogsMusic findById(long id) {
        return repository.findById(id).orElse(null);
    }

    @CacheEvict(value = MUSIC_ID, key = "#music.id")
    public void deleteMusic(BlogsMusic music) {
        repository.delete(music);
    }

    public List<BlogsMusic> findByIds(List<Long> ids) {
        return Lists.newArrayList(repository.findAllById(ids));
    }

    public long countMusicUsedNum(long musicId) {
        return blogsService.countBlogsNumByMusicId(musicId);
    }

    public void saveAll(List<BlogsMusic> musics) {
        musics = repository.saveAll(musics);
        for (BlogsMusic music : musics) {
            redisService.del(BlogsRedisConstant.getMusicIdKey(music.getId()));
        }
    }
}
