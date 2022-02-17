package com.donglai.web.db.server.service;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Objects;

public class CommonQueryService {

    public static Criteria getCriteriaByTimes(Long startTime, Long endTime, Criteria criteria, ArrayList<Criteria> criteriaList, String timeFiled) {
        if (Objects.nonNull(startTime))
            criteriaList.add(Criteria.where(timeFiled).gte(startTime));
        if (Objects.nonNull(endTime))
            criteriaList.add(Criteria.where(timeFiled).lte(endTime));
        if (!CollectionUtils.isEmpty(criteriaList))
            criteria = criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        return criteria;
    }

    public static Criteria getCriteriaByTimes(Long startTime, Long endTime, Criteria criteria, String timeFiled) {
        return getCriteriaByTimes(startTime, endTime, criteria, new ArrayList<>(), timeFiled);
    }
}
