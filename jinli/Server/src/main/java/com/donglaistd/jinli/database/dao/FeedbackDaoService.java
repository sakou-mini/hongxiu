package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.Feedback;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.util.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class FeedbackDaoService {
    private final FeedbackRepository feedbackRepository;
    private final MongoTemplate mongoTemplate;
    public FeedbackDaoService(FeedbackRepository feedbackRepository, MongoTemplate mongoTemplate) {
        this.feedbackRepository = feedbackRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public Feedback save(Feedback feedback){
        return feedbackRepository.save(feedback);
    }

    public PageInfo<Feedback> findFeedbackPageInfo(Long startTime, Long endTime , String keyWords, PageRequest pageRequest){
        List<Criteria> criteriaList = new ArrayList<>();
        if(startTime != null) criteriaList.add(Criteria.where("createTime").gte(startTime));
        if(endTime != null) criteriaList.add(Criteria.where("createTime").lte(endTime));
        if(!StringUtils.isNullOrBlank(keyWords)) {
            Pattern pattern= Pattern.compile("^.*"+keyWords+".*$", Pattern.CASE_INSENSITIVE);
            criteriaList.add(Criteria.where("feedbackMessage").regex(pattern));
        }
        Criteria criteria = new Criteria();
        if(!criteriaList.isEmpty()) criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = Query.query(criteria);
        long total = mongoTemplate.count(query, Feedback.class);
        List<Feedback> content = mongoTemplate.find(query.with(pageRequest).with(Sort.by(Sort.Direction.ASC, "createTime")), Feedback.class);
        return new PageInfo<>(content, total);
    }
}
