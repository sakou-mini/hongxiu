package com.donglai.model.db.service.common;

import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.repository.common.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.donglai.common.constant.RedisConstant.USER_ACCOUNT;
import static com.donglai.common.constant.RedisConstant.USER_ID;

@Service
@Transactional
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    private void clearCache(String uid,String accountId){
        redisTemplate.delete(USER_ID+ "::" + uid);
        redisTemplate.delete(USER_ACCOUNT+ "::" + accountId);
    }

    @Cacheable(value = USER_ID,key="#uid",unless="#result == null")
    public User findById(String uid) {
        return userRepository.findById(uid).orElse(null);
    }

    @Cacheable(value = USER_ACCOUNT,key="#accountId",unless="#result == null")
    public User findByAccountId(String accountId) {
        return this.userRepository.findByAccountId(accountId);
    }

    @Caching(put = {
            @CachePut(value = USER_ID, key = "#user.id", unless="#result == null"),
            @CachePut(value = USER_ACCOUNT, key = "#user.accountId", unless="#result == null")
    })
    public User save(User user) {
        return this.userRepository.save(user);
    }

    public List<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    public User findByLiveUserId(String id) {
        return userRepository.findByLiveUserId(id);
    }

    public List<User> saveAll(List<User> users) {
        users = userRepository.saveAll(users);
        for (User user : users) {
            clearCache(user.getId(),user.getAccountId());
        }
        return users;
    }

    public long count() {
        return userRepository.count();
    }

    public List<User> findByIds(List<String> ids){
       return userRepository.findAllByIdIn(ids);
    }

    public User findByOtherId(String otherId){
        return userRepository.findByOtherId(otherId);
    }
    public User findByUuid(String uuid){
        return userRepository.findByUuid(uuid);
    }
}
