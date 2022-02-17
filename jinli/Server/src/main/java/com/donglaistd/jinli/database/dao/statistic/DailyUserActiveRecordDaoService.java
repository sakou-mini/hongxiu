package com.donglaistd.jinli.database.dao.statistic;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.annotation.AutoIncKey;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.statistic.DailyUserActiveRecord;
import com.donglaistd.jinli.http.dto.request.UserListRequest;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.util.StringUtils;
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
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class DailyUserActiveRecordDaoService {
    private static  final Logger LOGGER = Logger.getLogger(DailyUserActiveRecordDaoService.class.getName());
    @Autowired
    DailyUserActiveRecordRepository repository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UserDaoService userDaoService;


    public DailyUserActiveRecord save(DailyUserActiveRecord record){
        return repository.save(record);
    }

    public List<DailyUserActiveRecord> saveAll(List<DailyUserActiveRecord> records){
        return repository.saveAll(records);
    }

    public List<DailyUserActiveRecord> findAll(){
        return repository.findAll();
    }

    public PageInfo<DailyUserActiveRecord> queryDailyUserActive(UserListRequest request) {
        Criteria criteria = Criteria.where("platform").is(request.getPlatformType());
        List<Criteria> criteriaList = new ArrayList<>();
        if(Objects.nonNull(request.getStartTime()))
            criteriaList.add(Criteria.where("dailyTime").gte(request.getStartTime()));
        if(Objects.nonNull(request.getEndTime()))
            criteriaList.add(Criteria.where("dailyTime").lte(request.getEndTime()));
        if(!StringUtils.isNullOrBlank(request.getDisplayName())){
            User user = userDaoService.findByDisplayName(request.getDisplayName());
            if(Objects.nonNull(user)) request.setUserId(user.getId());
            else return new PageInfo<>(new ArrayList<>(0), 0);
        }
        if(!StringUtils.isNullOrBlank(request.getUserId()))
            criteriaList.add(Criteria.where("userId").is(request.getUserId()));
        if(!criteriaList.isEmpty())
            criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC,"dailyTime"));
        long totalNum = mongoTemplate.count(query, DailyUserActiveRecord.class);
        PageRequest pageRequest = request.getPageRequest();
        if(pageRequest!=null) {
            query = query.with(pageRequest);
        }
        List<DailyUserActiveRecord> records = mongoTemplate.find(query, DailyUserActiveRecord.class);
        return new PageInfo<>(records,totalNum);
    }

    public void deleteAllIfUserIfIsNull(){
        List<DailyUserActiveRecord> all = repository.findAll();
        List<DailyUserActiveRecord> errorData = all.stream().filter(record -> StringUtils.isNullOrBlank(record.getUserId())).collect(Collectors.toList());
        repository.deleteAll(errorData);
    }
}
