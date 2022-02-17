package com.donglaistd.jinli.database.dao.statistic;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.statistic.DayLiveDataTotal;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DayLiveDataTotalDaoService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    DayLiveDataTotalRepository dayLiveDataTotalRepository;

    public List<DayLiveDataTotal> findByPlatformAndTimeBetween(Constant.PlatformType platform, long startTime,long endTime){
        Criteria criteria = Criteria.where("platform").is(platform).and("recordTime").gte(startTime).lte(endTime);
        return mongoTemplate.find(Query.query(criteria).with(Sort.by(Sort.Direction.ASC, "recordTime")), DayLiveDataTotal.class);
    }

    public DayLiveDataTotal save(DayLiveDataTotal dayLiveDataTotal){
        return dayLiveDataTotalRepository.save(dayLiveDataTotal);
    }

    @Transactional
    public List<DayLiveDataTotal> saveAll(List<DayLiveDataTotal> dayLiveDataTotals){
        return dayLiveDataTotalRepository.saveAll(dayLiveDataTotals);
    }

    public PageInfo<DayLiveDataTotal> findByPagePlatformAndTimeBetween(PlatformTimeRequest request) {
        List<Criteria> criteriaList = CommonCriteriaUtil.platformTimeQueryCriteria(request.getPlatformType(), request.getStartTime(), request.getEndTime());
        Criteria criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        long totalNum = mongoTemplate.count(Query.query(criteria), DayLiveDataTotal.class);
        List<DayLiveDataTotal> dataList = mongoTemplate.find(Query.query(criteria).with(PageRequest.of(request.getPage(), request.getSize()))
                .with(Sort.by(Sort.Direction.DESC, "recordTime")), DayLiveDataTotal.class);
        return new PageInfo<>(dataList, totalNum);
    }

    public DayLiveDataTotal findByRecordTimeAndPlatform(Constant.PlatformType platform,long time){
       return dayLiveDataTotalRepository.findByPlatformAndRecordTime(platform, time);
    }
}
