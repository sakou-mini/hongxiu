package com.donglaistd.jinli.database.dao.statistic;

import com.donglaistd.jinli.database.entity.statistic.DayUserDataTotal;
import com.donglaistd.jinli.http.dto.request.PlatformTimeRequest;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.util.CommonCriteriaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DayUserDataTotalDaoService {
    @Autowired
    DayUserDataTotalRepository repository;
    @Autowired
    MongoTemplate mongoTemplate;

    public DayUserDataTotal save(DayUserDataTotal dayUserDataTotal) {
        return repository.save(dayUserDataTotal);
    }

    public List<DayUserDataTotal> saveAll(List<DayUserDataTotal> dayUserDataTotals) {
        return repository.saveAll(dayUserDataTotals);
    }

    public PageInfo<DayUserDataTotal> queryPlatformDataByTimesAndPage(PlatformTimeRequest request){
        Criteria criteria = Criteria.where("platform").is(request.getPlatformType());
        List<Criteria> criteriaList = CommonCriteriaUtil.platformTimeQueryCriteria(request.getPlatformType(), request.getStartTime(), request.getEndTime());
        criteria = criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = Query.query(criteria);
        long totalNum = mongoTemplate.count(query, DayUserDataTotal.class);
        List<DayUserDataTotal> content = mongoTemplate.find(query.with(Sort.by(Sort.Direction.DESC, "recordTime"))
                .with(PageRequest.of(request.getPage(), request.getSize())), DayUserDataTotal.class);
        return new PageInfo<>(content, totalNum);
    }
}
