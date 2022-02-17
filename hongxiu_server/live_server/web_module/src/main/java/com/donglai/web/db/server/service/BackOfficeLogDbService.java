package com.donglai.web.db.server.service;

import com.donglai.web.db.backoffice.entity.BackOfficeLog;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.BackOfficeLogFindListRequest;
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

import static com.donglai.web.constant.DataBaseFiledConstant.BLOG_ID;

/**
 * @author Moon
 * @date 2021-12-31 11:20
 */
@Service
public class BackOfficeLogDbService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public PageInfo<BackOfficeLog> conditionGetLabel(BackOfficeLogFindListRequest request) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(request.getRes())) {
            criteria.and("res").is(request.getRes());
        }
        CommonQueryService.getCriteriaByTimes(
                request.getAddTimeStart(),
                request.getAddTimeEnd(),
                criteria, "createdTime");

        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, BackOfficeLog.class);
        List<BackOfficeLog> reports = mongoTemplate.find(query.with(pageable).with(Sort.by(Sort.Direction.DESC, "createdTime")), BackOfficeLog.class);

        return new PageInfo<>(pageable, reports, count);

    }
}
