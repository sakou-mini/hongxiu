package com.donglaistd.jinli.util;

import com.donglaistd.jinli.Constant;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;

public class CommonCriteriaUtil {

    public static List<Criteria> platformTimeQueryCriteria(Constant.PlatformType platformType, Long startTime, Long endTime) {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("platform").is(platformType));
        if(startTime !=null){
            criteriaList.add(Criteria.where("recordTime").gte(startTime));
        }
        if(endTime !=null){
            criteriaList.add(Criteria.where("recordTime").lte(endTime));
        }
        return criteriaList;
    }

    public static Criteria getUserCriteriaByUserId(Criteria criteria,String userId, Constant.PlatformType platformType){
        if(!platformType.equals(Constant.PlatformType.PLATFORM_JINLI)) {
            criteria.and("platformUserId").is(userId);
        }else{
            criteria.and("_id").is(userId);
        }
        return criteria;
    }
}
