package com.donglai.web.db.server.service;

import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.live.LiveUser;
import com.donglai.model.db.service.common.UserService;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.LiveUserListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
public class LiveUserDbService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UserService userService;

    public PageInfo<LiveUser> liveUserList(LiveUserListRequest request){
        LookupOperation user = LookupOperation.newLookup().from("user").localField("userId").foreignField("_id").as("user");
        Criteria criteria = new Criteria();
        if(!StringUtils.isNullOrBlank(request.getNickname())) {
            criteria.and("user.nickname").is(request.getNickname());
        }
        if(!StringUtils.isNullOrBlank(request.getLiveUserId())){
            criteria.and("_id").is(request.getLiveUserId());
        }
        if(!StringUtils.isNullOrBlank(request.getRoomId())){
            criteria.and("roomId").is(request.getRoomId());
        }
        if(request.getStatus() >= 0){
            criteria.and("user.status").is(request.getStatus());
        }
        return commonPageQuery(PageRequest.of(request.getPage(),request.getSize()),criteria,user,"lastLiveTime");
    }

    private  PageInfo<LiveUser> commonPageQuery(PageRequest pageInfo, Criteria criteria, LookupOperation lookupOperation, String sortedFiled){
        int totalNum = mongoTemplate.aggregate(Aggregation.newAggregation(lookupOperation, Aggregation.match(criteria)), LiveUser.class, LiveUser.class).getMappedResults().size();
        int page = pageInfo.getPageNumber();
        int size = pageInfo.getPageSize();
        Aggregation agg = Aggregation.newAggregation(lookupOperation, Aggregation.match(criteria),Aggregation.sort(Sort.by(Sort.Direction.DESC,sortedFiled)),
                Aggregation.skip(page > 1 ? (page - 1) * size : 0L), Aggregation.limit(size));
        return new PageInfo<>(page,size,totalNum,mongoTemplate.aggregate(agg, LiveUser.class, LiveUser.class).getMappedResults());
    }
}
