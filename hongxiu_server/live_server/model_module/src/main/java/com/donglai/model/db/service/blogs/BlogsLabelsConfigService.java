package com.donglai.model.db.service.blogs;

import com.donglai.common.service.RedisService;
import com.donglai.model.db.entity.blogs.BlogsLabelsConfig;
import com.donglai.model.db.repository.blogs.BlogsLabelsConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.donglai.common.constant.BlogsRedisConstant.BLOGS_LABELS_ALL;

@Service
public class BlogsLabelsConfigService {
    @Autowired
    BlogsLabelsConfigRepository repository;
    @Autowired
    RedisService redisService;

    public BlogsLabelsConfig save(BlogsLabelsConfig labelsConfig) {
        redisService.del(BLOGS_LABELS_ALL);
        return repository.save(labelsConfig);
    }

    @Transactional
    public List<BlogsLabelsConfig> saveAll(List<BlogsLabelsConfig> labelsConfigs) {
        redisService.del(BLOGS_LABELS_ALL);
        return repository.saveAll(labelsConfigs);
    }

    @Cacheable(value = BLOGS_LABELS_ALL, unless = "#result == null")
    public List<BlogsLabelsConfig> findAll() {
        return repository.findAll();
    }

    public BlogsLabelsConfig findId(int id) {
        return repository.findById(id).orElse(null);
    }

    public List<BlogsLabelsConfig> findByLabelsIdIn(List<Integer> labels) {
        return repository.findByIdIn(labels);
    }

    public List<BlogsLabelsConfig> delListIn(List<Integer> labels) {
        return repository.deleteByIdIn(labels);
    }

    public BlogsLabelsConfig findByLabelName(String labelName) {
        return repository.findByLabelName(labelName);
    }
}
