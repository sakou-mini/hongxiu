package com.donglai.web.db.server.service;

import com.donglai.model.db.entity.live.ReportComment;
import com.donglai.model.db.entity.live.ReportUser;
import com.donglai.model.db.entity.live.ReportVideo;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.ReportFindListRequest;
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
 * @date 2021-12-27 13:57
 */
@Service
public class ReportDbService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public PageInfo<ReportVideo> conditionGetVideo(ReportFindListRequest request) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(request.getHandel())) {
            criteria.and("handel").is(request.getHandel());
        }
        CommonQueryService.getCriteriaByTimes(
                request.getAddStartTime(),
                request.getAddEndTime(),
                criteria, "createdTime");
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, ReportVideo.class);
        List<ReportVideo> reports = mongoTemplate.find(query.with(pageable).with(Sort.by(Sort.Direction.DESC, "createdTime")), ReportVideo.class);

        return new PageInfo<>(pageable, reports, count);
    }

    public PageInfo<ReportComment> conditionGetComment(ReportFindListRequest request) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(request.getHandel())) {
            criteria.and("handel").is(request.getHandel());
        }
        CommonQueryService.getCriteriaByTimes(
                request.getAddStartTime(),
                request.getAddEndTime(),
                criteria, "createdTime");
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, ReportComment.class);
        List<ReportComment> reports = mongoTemplate.find(query.with(pageable), ReportComment.class);

        return new PageInfo<>(pageable, reports, count);
    }

    public PageInfo<ReportUser> conditionGetUser(ReportFindListRequest request) {
        Criteria criteria = new Criteria();
        if (Objects.nonNull(request.getHandel())) {
            criteria.and("handel").is(request.getHandel());
        }
        CommonQueryService.getCriteriaByTimes(
                request.getAddStartTime(),
                request.getAddEndTime(),
                criteria, "createdTime");
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, ReportUser.class);
        List<ReportUser> reports = mongoTemplate.find(query.with(pageable), ReportUser.class);

        return new PageInfo<>(pageable, reports, count);
    }
}
