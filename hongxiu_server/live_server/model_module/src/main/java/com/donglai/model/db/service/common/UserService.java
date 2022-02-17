package com.donglai.model.db.service.common;

import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.repository.common.UserRepository;
import com.donglai.model.service.impl.es.UserElasticsearchServiceImpl;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.donglai.common.constant.RedisConstant.USER_ACCOUNT;
import static com.donglai.common.constant.RedisConstant.USER_ID;

@Service
@Transactional
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    UserElasticsearchServiceImpl userElasticsearchService;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    MongoOperations mongoOperations;

    private void clearCache(String uid,String accountId) {
        redisTemplate.delete(USER_ID + "::" + uid);
        redisTemplate.delete(USER_ACCOUNT + "::" + accountId);
    }

    @Cacheable(value = USER_ID, key = "#uid", unless = "#result == null")
    public User findById(String uid) {
        return userRepository.findById(uid).orElse(null);
    }

    @Cacheable(value = USER_ACCOUNT, key = "#accountId", unless = "#result == null")
    public User findByAccountId(String accountId) {
        return this.userRepository.findByAccountId(accountId);
    }

    @Caching(put = {
            @CachePut(value = USER_ID, key = "#user.id", unless = "#result == null"),
            @CachePut(value = USER_ACCOUNT, key = "#user.accountId", unless = "#result == null")})
    public User save(User user) {
        user = this.userRepository.save(user);
        userElasticsearchService.updateIndex(user);
        return user;
    }

    public List<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    public User findByLiveUserId(String id) {
        return userRepository.findByLiveUserId(id);
    }

    @Transactional
    public List<User> saveAll(List<User> users) {
        users = userRepository.saveAll(users);
        for (User user : users) {
            clearCache(user.getId(),user.getAccountId());
        }
        userElasticsearchService.saveIndex(users);
        return users;
    }

    public long count() {
        return userRepository.count();
    }

    public List<User> findByIds(List<String> ids) {
        return userRepository.findAllByIdIn(ids);
    }

    public User findByUuid(String uuid) {
        return userRepository.findByUuid(uuid);
    }

    public User findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public List<User> findByNicknameLike(String keyword) {
        return userRepository.findByNicknameLike(keyword == null ? "" : keyword.replaceAll(" ", ""));
    }

    public List<User> findByUserIdInAndNicknameLike(Set<String> leaderIds, String name) {
        return userRepository.findByIdInAndNicknameLike(leaderIds, name);
    }

    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    //老用户数量（非游客，且注册日期小起始时间）
    public long countOldUserNumByTime(long time) {
        Criteria timeCriteria = Criteria.where("createTime").lte(time).and("tourist").is(false).and("lastLoginTime").gt(0);
        return mongoTemplate.count(Query.query(timeCriteria), User.class);
    }

    public List<User> findByTouristIs(boolean b) {
        return userRepository.findByTouristIs(b);
    }

    @Transactional
    public UpdateResult addUserCoin(String userId, long increaseAmount){
        User user = findById(userId);
        if(Objects.nonNull(user)) clearCache(user.getId(), user.getAccountId());
        return mongoOperations.updateFirst(new Query(Criteria.where("_id").is(userId)), new Update().inc("coin", increaseAmount), User.class);
    }
}
