package com.donglai.web.db.backoffice.service;

import com.donglai.web.db.backoffice.entity.BackOfficeLog;
import com.donglai.web.db.backoffice.repository.BackOfficeLogRepository;
import com.donglai.web.db.server.service.CommonQueryService;
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

/**
 * @author Moon
 * @date 2021-12-30 11:52
 */
@Service
public class BackOfficeLogService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private BackOfficeLogRepository repository;

    public BackOfficeLog save(BackOfficeLog backOfficeLog) {
        return repository.save(backOfficeLog);
    }


    public PageInfo<BackOfficeLog> findLogList(BackOfficeLogFindListRequest request) {
        Criteria criteria = new Criteria();
        CommonQueryService.getCriteriaByTimes(
                request.getStartTime(),
                request.getEndTime(),
                criteria, "createdTime");

        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, BackOfficeLog.class);
        if( request.getSize() >0){
            Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
            query = query.with(pageable);
        }
        List<BackOfficeLog> reports = mongoTemplate.find(query.with(Sort.by(Sort.Direction.DESC, "createdTime")), BackOfficeLog.class);
        return new PageInfo<>(request.getPage(), request.getSize(), count, reports);
    }




}
