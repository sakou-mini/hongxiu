package com.donglai.web.db.server.service;

import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.LiveRecord;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.LiveRecordService;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.reply.LiveRecordReply;
import com.donglai.web.web.dto.request.LiveRecordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LiveRecordDbService {
    @Autowired
    LiveRecordService liveRecordService;
    @Autowired
    UserService userService;
    @Autowired
    MongoTemplate mongoTemplate;

    public PageInfo<LiveRecordReply> liveRecordList(LiveRecordRequest request){
        Criteria criteria = new Criteria();
        if(!StringUtils.isNullOrBlank(request.getNickname())){
            List<String> userIds = userService.findByNickname(request.getNickname()).stream().map(User::getId).collect(Collectors.toList());
            criteria.and("userId").in(userIds);
        }
        if(!StringUtils.isNullOrBlank(request.getLiveUserId())){
            criteria.and("liveUserId").is(request.getLiveUserId());
        }
        if(!StringUtils.isNullOrBlank(request.getRoomId()))
            criteria.and("roomId").is(request.getRoomId());
        criteria = CommonQueryService.getCriteriaByTimes(request.getStartTime(), request.getEndTime(), criteria, "startTime");
        Query query = Query.query(criteria);
        int count = (int) mongoTemplate.count(query, LiveRecord.class);
        if(request.getSize() > 0){
            query.with(PageRequest.of(request.getPage() - 1, request.getSize())).with(Sort.by(Sort.Direction.DESC, "startTime"));
        }
        List<LiveRecordReply> contents = mongoTemplate.find(query, LiveRecord.class).stream().map(this::buildLiveRecordReply).collect(Collectors.toList());
        return new PageInfo<>(request.getPage(), request.getSize(), count, contents);
     }

     public LiveRecordReply buildLiveRecordReply(LiveRecord liveRecord){
         return new LiveRecordReply(userService.findById(liveRecord.getUserId()), liveRecord);
     }
}
