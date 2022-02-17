package com.donglai.web.db.server.service;

import com.donglai.model.db.entity.back.RecommendUser;
import com.donglai.model.db.entity.back.RecommendVideo;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.RecommendFindUserListRequest;
import com.donglai.web.web.dto.request.RecommendFindVideoListRequest;
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
 * @date 2022-01-05 14:04
 */
@Service
public class RecommendDbService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public PageInfo<RecommendVideo> conditionGetRecommendVideodList(RecommendFindVideoListRequest request) {

        Criteria criteria = new Criteria();
        if (Objects.nonNull(request.getUserId())) {
            criteria.and("blogUserId").is(request.getUserId());
        }
        CommonQueryService.getCriteriaByTimes(
                request.getCreatedTimeStart(),
                request.getCreatedTimeEnd(),
                criteria, "createdTime");

        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, RecommendVideo.class);
        List<RecommendVideo> reports = mongoTemplate.find(query.with(pageable).with(Sort.by(Sort.Direction.DESC, "createdTime")), RecommendVideo.class);

        return new PageInfo<>(pageable, reports, count);
    }

    public PageInfo<RecommendUser> conditionGetRecommendUserList(RecommendFindUserListRequest request) {

        Criteria criteria = new Criteria();
        if (Objects.nonNull(request.getUserId())) {
            criteria.and("userId").is(request.getUserId());
        }
        if (Objects.nonNull(request.getStatus())) {
            criteria.and("status").is(request.getStatus());
        }
        CommonQueryService.getCriteriaByTimes(
                request.getCreatedTimeStart(),
                request.getCreatedTimeEnd(),
                criteria, "joinTime");

        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, RecommendUser.class);
        List<RecommendUser> reports = mongoTemplate.find(query.with(pageable), RecommendUser.class);

        return new PageInfo<>(pageable, reports, count);
    }
}
