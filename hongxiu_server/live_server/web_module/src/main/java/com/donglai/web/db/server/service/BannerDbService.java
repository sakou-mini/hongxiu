package com.donglai.web.db.server.service;

import com.donglai.model.db.entity.live.Banner;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.BannerFindListRequest;
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
 * @date 2022-01-04 16:25
 */
@Service
public class BannerDbService {

    @Autowired
    private MongoTemplate mongoTemplate;


    public PageInfo<Banner> conditionGetBanner(BannerFindListRequest request) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(request.getTitle())) {
            criteria.and("title").is(request.getTitle());
        }
        if (Objects.nonNull(request.getStatus())) {
            criteria.and("status").is(request.getStatus());

        }
        CommonQueryService.getCriteriaByTimes(
                request.getStartTime(),
                request.getEndTime(),
                criteria, "startTime");

        CommonQueryService.getCriteriaByTimes(
                request.getAddStartTime(),
                request.getAddEndTime(),
                criteria, "createdTime");


        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, Banner.class);
        List<Banner> labels = mongoTemplate.find(query.with(pageable).with(Sort.by(Sort.Direction.DESC, "createdTime")), Banner.class);

        return new PageInfo<>(pageable, labels, count);
    }

}
