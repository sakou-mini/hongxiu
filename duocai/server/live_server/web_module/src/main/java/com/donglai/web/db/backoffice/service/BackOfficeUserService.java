package com.donglai.web.db.backoffice.service;

import com.donglai.common.service.RedisService;
import com.donglai.common.util.StringUtils;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.db.backoffice.repository.BackOfficeUserRepository;
import com.donglai.web.db.server.service.CommonQueryService;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.BackOfficeUserFindListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.donglai.common.constant.RedisConstant.USER_ACCOUNT;
import static com.donglai.common.constant.RedisConstant.USER_ID;
import static com.donglai.web.constant.DataBaseFiledConstant.*;
import static com.donglai.web.constant.WebConstant.BACKOFFICE_USER_ID_KEY;
import static com.donglai.web.constant.WebConstant.BACKOFFICE_USER_NAME_KEY;

@Service
public class BackOfficeUserService {
    @Autowired
    BackOfficeUserRepository backOfficeUserRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    RedisService redisService;

    @Cacheable(value = BACKOFFICE_USER_NAME_KEY,key="#username",unless="#result == null")
    public BackOfficeUser findByUserName(String username){
        return backOfficeUserRepository.findByUsername(username);
    }

    @Cacheable(value = BACKOFFICE_USER_ID_KEY,key="#id",unless="#result == null")
    public BackOfficeUser findById(String id) {
        return backOfficeUserRepository.findById(id).orElse(null);
    }

    @Caching(put = {
            @CachePut(value = BACKOFFICE_USER_ID_KEY, key = "#result.id", unless="#result == null"),
            @CachePut(value = BACKOFFICE_USER_NAME_KEY, key = "#result.username", unless="#result == null")
    })
    public BackOfficeUser save(BackOfficeUser backOfficeUser) {
        return backOfficeUserRepository.save(backOfficeUser);
    }

    @Caching(evict = {
            @CacheEvict(value = BACKOFFICE_USER_ID_KEY,key = "#backOfficeUser.id", condition = "#backOfficeUser != null"),
            @CacheEvict(value = BACKOFFICE_USER_NAME_KEY, key = "#backOfficeUser.username", condition = "#backOfficeUser != null")
    })
    public void deleteBackOfficeUser(BackOfficeUser backOfficeUser){
        backOfficeUserRepository.delete(backOfficeUser);
    }

    public PageInfo<BackOfficeUser> findBackOfficeUserList(BackOfficeUserFindListRequest request) {
        Criteria criteria = Criteria.where(BACKOFFICE_USER_ACCOUNT_NON_LOCKED).is(true);
        if (!StringUtils.isNullOrBlank(request.getRoleGroup())) criteria.and("roles.$id").is(request.getRoleGroup());
        if (Objects.nonNull(request.getStatus())) criteria.and(BACKOFFICE_USER_STATUS).is(request.getStatus());
        criteria = CommonQueryService.getCriteriaByTimes(request.getCreatedTimeStart(), request.getCreatedTimeEnd(), criteria, BACKOFFICE_USER_CREATE_TIME);
        PageRequest pageRequest = PageRequest.of(request.getPage() - 1, request.getSize());
        Query query = Query.query(criteria);
        long totalNum = mongoTemplate.count(query, BackOfficeUser.class);
        List<BackOfficeUser> backOfficeUsers = mongoTemplate.find(query.with(pageRequest).with(Sort.by(Sort.Direction.DESC, BACKOFFICE_USER_CREATE_TIME)), BackOfficeUser.class);
        return new PageInfo<>(pageRequest, backOfficeUsers, totalNum);

    }

    public List<BackOfficeUser> findByIdIn(List<String> ids) {
        return backOfficeUserRepository.findByIdIn(ids);
    }

    private void clearCache(String id, String name) {
        redisService.del(BACKOFFICE_USER_ID_KEY + "::" + id);
        redisService.del(BACKOFFICE_USER_NAME_KEY + "::" + name);
    }

    public List<BackOfficeUser> saveAll(List<BackOfficeUser> backOfficeUsers) {
        backOfficeUsers = backOfficeUserRepository.saveAll(backOfficeUsers);
        backOfficeUsers.forEach(backOfficeUser -> clearCache(backOfficeUser.getId(), backOfficeUser.getUsername()));
        return backOfficeUsers;
    }

    public void deleteAll(){
        List<BackOfficeUser> backOfficeUsers = backOfficeUserRepository.findAll();
        backOfficeUsers.forEach(backOfficeUser -> clearCache(backOfficeUser.getId(), backOfficeUser.getUsername()));
        backOfficeUserRepository.deleteAll();
    }

    public List<BackOfficeUser> findByHasRoles(List<Role> roles) {
        return backOfficeUserRepository.findAllByRolesContains(roles);
    }

}
