package com.donglai.web.db.server.service;

import com.donglai.model.db.entity.live.FeedBack;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.FeedbackFindListRequest;
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
 * @date 2021-12-28 16:51
 */
@Service
public class FeedBackDbService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public PageInfo<FeedBack> conditionGetFeedBack(FeedbackFindListRequest request) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(request.getType())) {
            criteria.and("type").is(request.getType());
        }
        if (Objects.nonNull(request.getStatus())) {
            criteria.and("status").is(request.getStatus());
        }
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, FeedBack.class);
        List<FeedBack> reports = mongoTemplate.find(query.with(pageable).with(Sort.by(Sort.Direction.DESC, "createdTime")), FeedBack.class);

        return new PageInfo<>(pageable, reports, count);
    }
}
