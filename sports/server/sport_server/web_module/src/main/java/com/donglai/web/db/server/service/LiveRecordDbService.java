package com.donglai.web.db.server.service;

import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.live.LiveRecord;
import com.donglai.model.db.entity.sport.SportEvent;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.LiveRecordService;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.LiveRecordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LiveRecordDbService {
    @Autowired
    LiveRecordService liveRecordService;
    @Autowired
    UserService userService;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    SportEventQueryService sportEventQueryService;

    public LiveRecord findByEventId(String eventId) {
        Criteria criteria = Criteria.where("eventId").is(eventId);
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC, "endTime")).with(PageRequest.of(0,1));
        return mongoTemplate.findOne(query, LiveRecord.class);
    }

    public PageInfo<LiveRecord> liveRecordList(LiveRecordRequest request){
        Criteria criteria = new Criteria();
        if(!StringUtils.isNullOrBlank(request.getLiveUserId())){
            criteria.and("liveUserId").is(request.getLiveUserId());
        }
        if(Objects.nonNull(request.getStartTime()) || Objects.nonNull(request.getEndTime())){
            List<String> eventIds = sportEventQueryService.findByTimes(request.getStartTime(), request.getEndTime()).stream().map(SportEvent::getId).collect(Collectors.toList());
            criteria.and("eventId").in(eventIds);
        }
        Query query = Query.query(criteria);
        int count = (int) mongoTemplate.count(query, LiveRecord.class);
        if(request.getSize() > 0){
            query.with(PageRequest.of(request.getPage() - 1, request.getSize())).with(Sort.by(Sort.Direction.DESC, "startTime"));
        }
        List<LiveRecord> contents = mongoTemplate.find(query, LiveRecord.class);
        return new PageInfo<>(request.getPage(), request.getSize(), count, contents);
    }

}
