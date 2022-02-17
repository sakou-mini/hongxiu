package com.donglai.web.db.backoffice.service;

import com.donglai.common.util.StringUtils;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.repository.BackOfficeUserRepository;
import com.donglai.web.response.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.donglai.web.constant.DataBaseFiledConstant.BACKOFFICE_USER_STATUS;
import static com.donglai.web.constant.WebConstant.BACKOFFICE_USER_ID_KEY;
import static com.donglai.web.constant.WebConstant.BACKOFFICE_USER_NAME_KEY;

@Service
public class BackOfficeUserService {
    @Autowired
    BackOfficeUserRepository backOfficeUserRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    @Cacheable(value = BACKOFFICE_USER_NAME_KEY, key = "#username", unless = "#result == null")
    public BackOfficeUser findByUserName(String username) {
        return backOfficeUserRepository.findByUsername(username);
    }

    @Cacheable(value = BACKOFFICE_USER_ID_KEY, key = "#id", unless = "#result == null")
    public BackOfficeUser findById(String id) {
        return backOfficeUserRepository.findById(id).orElse(null);
    }

    @Caching(put = {
            @CachePut(value = BACKOFFICE_USER_ID_KEY, key = "#result.id", unless = "#result == null"),
            @CachePut(value = BACKOFFICE_USER_NAME_KEY, key = "#result.username", unless = "#result == null")
    })
    public BackOfficeUser save(BackOfficeUser backOfficeUser) {
        return backOfficeUserRepository.save(backOfficeUser);
    }


    //statue -1 query all
    public PageInfo<BackOfficeUser> pageQuery(String roleId, Boolean statue, int page, int size) {
        Criteria criteria = new Criteria();
        if (!StringUtils.isNullOrBlank(roleId)) criteria.and("roles.$id").is(roleId);
        if (Objects.nonNull(statue)) criteria.and(BACKOFFICE_USER_STATUS).is(statue);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Query query = Query.query(criteria);
        long totalNum = mongoTemplate.count(query, BackOfficeUser.class);
        List<BackOfficeUser> backOfficeUsers = mongoTemplate.find(query.with(pageRequest).with(Sort.by(Sort.Direction.DESC, "createTime")), BackOfficeUser.class);
        return new PageInfo<>(pageRequest, backOfficeUsers, totalNum);
    }

    public List<BackOfficeUser> findByIdIn(List<String> ids) {
        return backOfficeUserRepository.findByIdIn(ids);
    }

    public List<BackOfficeUser> saveAll(List<BackOfficeUser> backOfficeUsers) {
        return backOfficeUserRepository.saveAll(backOfficeUsers);
    }

    public List<BackOfficeUser> deleteByIdIn(List<String> ids) {
        return backOfficeUserRepository.deleteByIdIn(ids);
    }
}
