package com.donglai.web.db.server.service;

import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.sport.SportEvent;
import com.donglai.model.db.entity.sport.SportLiveSchedule;
import com.donglai.model.db.service.sport.SportLiveScheduleService;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.HistoryEventRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.donglai.web.constant.DataBaseFiledConstant.EVENT_DATE_TIME;
import static com.donglai.web.constant.DataBaseFiledConstant.EVENT_ID;
import static com.donglai.web.constant.WebConstant.EVENT_OVER_HOUR;


@Service
public class SportEventQueryService {
    @Autowired
    SportLiveScheduleService sportLiveScheduleService;
    @Autowired
    MongoTemplate mongoTemplate;

    public PageInfo<SportEvent> queryActiveSportEvent(PageRequest pageRequest){
        //活跃的赛事，大于最近5小时内的
        long raceOverTime = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(EVENT_OVER_HOUR);
        Criteria criteria = Criteria.where(EVENT_DATE_TIME).gte(raceOverTime);
        Query query = new Query(criteria);
        long totalNum = mongoTemplate.count(query, SportEvent.class);
        List<SportEvent> result = mongoTemplate.find(query.with(pageRequest).with(Sort.by(Sort.Direction.DESC, EVENT_DATE_TIME)), SportEvent.class);
        return new PageInfo<>(pageRequest.getPageNumber(), pageRequest.getPageSize(), totalNum, result);
    }

    /*查询过期赛事*/
    public PageInfo<SportEvent> getOverSportEvent(HistoryEventRequest request){
        long raceOverTime = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(EVENT_OVER_HOUR);
        Criteria criteria = Criteria.where(EVENT_DATE_TIME).lt(raceOverTime);
        Query query = Query.query(criteria);
        long totalNum = mongoTemplate.count(query, SportEvent.class);
        PageRequest pageRequest = PageRequest.of(request.getPage() - 1, request.getSize());
        List<SportEvent> result = mongoTemplate.find(query.with(pageRequest).with(Sort.by(Sort.Direction.DESC, EVENT_DATE_TIME)), SportEvent.class);
        return new PageInfo<>(pageRequest.getPageNumber(), pageRequest.getPageSize(), totalNum, result);
    }


    public List<SportEvent> findByTimes(Long startTime,Long endTime){
        Criteria criteria = new Criteria();
        CommonQueryService.getCriteriaByTimes(startTime, endTime, criteria, EVENT_DATE_TIME);
        return mongoTemplate.find(Query.query(criteria), SportEvent.class);
    }
}
