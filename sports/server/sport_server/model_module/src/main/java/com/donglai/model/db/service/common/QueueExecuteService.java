package com.donglai.model.db.service.common;

import com.donglai.common.constant.RedisConstant;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.repository.common.QueueExecuteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class QueueExecuteService {
    @Autowired
    MongoTemplate secondaryMongoTemplate;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    @Autowired
    QueueExecuteService queueExecuteService2;
    @Autowired
    QueueExecuteRepository queueExecuteRepository;

    @Caching(put = {
            @CachePut(value = RedisConstant.QUEUE_EXECUTE_INFO, key = "#queueExecute.id", unless = "#result == null "),
            @CachePut(value = RedisConstant.QUEUE_REFTYPE_REFID_INFO, key = "#queueExecute.triggerType+':'+#queueExecute.refId", unless = "#result == null ")})
    public QueueExecute save(QueueExecute queueExecute)
    {
        return queueExecuteRepository.save(queueExecute);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisConstant.QUEUE_EXECUTE_INFO, key = "#queueExecute.id",condition = "#queueExecute!=null"),
            @CacheEvict(value = RedisConstant.QUEUE_REFTYPE_REFID_INFO, key = "#queueExecute.triggerType+':'+#queueExecute.refId",condition = "#queueExecute!=null")
    })
    public void del(QueueExecute queueExecute)
    {
        queueExecuteRepository.delete(queueExecute);
    }

    @Cacheable(value = RedisConstant.QUEUE_EXECUTE_INFO, key = "#id", unless = "#result==null")
    public QueueExecute findById(String id)
    {
        return queueExecuteRepository.findById(id).orElse(null);
    }

    @Cacheable(value = RedisConstant.QUEUE_REFTYPE_REFID_INFO, key = "#queueType+':'+#refId", unless = "#result==null")
    public QueueExecute findByQueueTypeAndRefId(int queueType,String refId) {
        return queueExecuteRepository.findByRefIdAndTriggerType(refId, queueType);
    }

    @Transactional
    public void deleteByQueueTypeAndRefId(int queueType,String refId) {
        QueueExecute queueExecute = queueExecuteRepository.findByRefIdAndTriggerType(refId, queueType);
        queueExecuteService2.del(queueExecute);
    }
}
