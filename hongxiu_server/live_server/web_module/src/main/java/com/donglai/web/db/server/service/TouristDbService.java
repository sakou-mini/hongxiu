package com.donglai.web.db.server.service;

import com.donglai.model.db.entity.common.User;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.TouristListRequest;
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
 * @date 2021-12-22 10:55
 */
@Service
public class TouristDbService {

    @Autowired
    MongoTemplate mongoTemplate;

    public PageInfo<User> conditionGetTourist(TouristListRequest request) {
        Criteria criteria = Criteria.where("tourist").is(true);

        if (Objects.nonNull(request.getTouristId())) {
            criteria.and("accountId").is(request.getTouristId());
        }

        if (Objects.nonNull(request.getLastIp())) {
            criteria.and("lastLoginLoginIp").is(request.getLastIp());
        }
        if (Objects.nonNull(request.getFirstIp())) {
            criteria.and("firstLoginIp").is(request.getFirstIp());
        }
        CommonQueryService.getCriteriaByTimes(
                request.getLastLoginTimeStart(),
                request.getLastLoginTimeEnd(),
                criteria, "lastLoginTime");

        CommonQueryService.getCriteriaByTimes(
                request.getFirstLoginTimeStart(),
                request.getFirstLoginTimeEnd(),
                criteria, "firstLoginTime");

        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, User.class);
        List<User> users = mongoTemplate.find(query.with(pageable).with(Sort.by(Sort.Direction.DESC, "lastLoginIp")), User.class);
        return new PageInfo<>(pageable, users, count);
    }

}
