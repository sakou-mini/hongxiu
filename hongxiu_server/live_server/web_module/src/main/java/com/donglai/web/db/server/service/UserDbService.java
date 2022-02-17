package com.donglai.web.db.server.service;

import com.donglai.model.db.entity.common.User;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.UserListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Moon
 * @date 2021-12-21 11:15
 */
@Service
public class UserDbService {

    @Autowired
    MongoTemplate mongoTemplate;

    public PageInfo<User> conditionGetUser(UserListRequest userListRequest) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(userListRequest.getNickname())) {
            criteria.and("nickname").is(userListRequest.getNickname());
        }
        if (Objects.nonNull(userListRequest.getUserId())) {
            criteria.and("id").is(userListRequest.getUserId());
        }
        if (Objects.nonNull(userListRequest.getLoginType())) {
            criteria.and("source").is(userListRequest.getLoginType());
        }
        if (Objects.nonNull(userListRequest.getPhone())) {
            criteria.and("phoneNumber").is(userListRequest.getPhone());
        }
        if (Objects.nonNull(userListRequest.getStatus())) {
            criteria.and("status").is(userListRequest.getStatus());
        }
        criteria.and("tourist").is(false);
        CommonQueryService.getCriteriaByTimes(
                userListRequest.getLastLoginTimeStart(),
                userListRequest.getLastLoginTimeEnd(),
                criteria, "lastLoginTime");

        CommonQueryService.getCriteriaByTimes(
                userListRequest.getJoinTimeStart(),
                userListRequest.getJoinTimeEnd(),
                criteria, "createTime");
        Pageable pageable = PageRequest.of(userListRequest.getPage() - 1, userListRequest.getSize());
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, User.class);
        List<User> users = mongoTemplate.find(query.with(pageable).with(Sort.by(Sort.Direction.DESC, "lastLoginIp")), User.class);
        return new PageInfo<>(pageable, users, count);
    }
}
