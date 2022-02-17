package com.donglai.web.db.server.service;

import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.FindConditionListRequest;
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
 * @date 2021-12-29 14:27
 */
@Service
public class RoleDbService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public PageInfo<Role> findConditionList(FindConditionListRequest request) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(request.getStatus())) {
            criteria.and("status").is(request.getStatus());
        }
        CommonQueryService.getCriteriaByTimes(
                request.getCreatedTimeStart(),
                request.getCreatedTimeEnd(),
                criteria, "createdTime");
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, Role.class);
        List<Role> reports = mongoTemplate.find(query.with(pageable).with(Sort.by(Sort.Direction.DESC, "createdTime")), Role.class);

        return new PageInfo<>(pageable, reports, count);
    }
}
