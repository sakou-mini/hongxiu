package com.donglai.web.db.server.service;

import com.donglai.model.db.entity.common.Keyword;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.KeywordFindListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Moon
 * @date 2021-12-28 14:14
 */
@Service
public class KeywordDbService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public PageInfo<Keyword> conditionGetKeyword(KeywordFindListRequest request) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(request.getKeyword())) {
            criteria.and("word").is(request.getKeyword());
        }
        if (Objects.nonNull(request.getStatus())) {
            criteria.and("status").is(request.getStatus());
        }
        CommonQueryService.getCriteriaByTimes(
                request.getCreatedTimeStart(),
                request.getCreatedTimeEnd(),
                criteria, "createdTime");
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, Keyword.class);
        List<Keyword> reports = mongoTemplate.find(query.with(pageable), Keyword.class);

        return new PageInfo<>(pageable, reports, count);
    }
}
