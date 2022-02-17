package com.donglai.model.db.service.common;

import com.donglai.common.service.RedisService;
import com.donglai.model.db.entity.common.Keyword;
import com.donglai.model.db.repository.common.KeywordRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.donglai.common.constant.RedisConstant.KEYWORDS;
import static com.donglai.common.constant.RedisConstant.SEPARATOR;

/**
 * @author Moon
 * @date 2021-12-28 13:48
 */
@Service
public class KeywordService {
    @Autowired
    private KeywordRepository keywordRepository;
    @Autowired
    RedisService redisService;

    @Cacheable(value = KEYWORDS, unless = "#result == null")
    public List<Keyword> findAll() {
        return keywordRepository.findAll();
    }

    public Keyword save(Keyword keyword) {
        cleanAndRestKeyWord();
        return keywordRepository.save(keyword);
    }

    public List<Keyword> findByIdIn(List<Long> ids) {
        return keywordRepository.findByIdIn(ids);
    }

    public List<Keyword> saveAll(List<Keyword> byIdIn) {
        cleanAndRestKeyWord();
        return keywordRepository.saveAll(byIdIn);
    }

    public List<Keyword> deleteByIdIn(List<Long> ids) {
        cleanAndRestKeyWord();
        return keywordRepository.deleteByIdIn(ids);
    }

    private void cleanAndRestKeyWord(){
        redisService.del(Lists.newArrayList(redisService.keys(KEYWORDS + SEPARATOR + "*")));
    }
}
