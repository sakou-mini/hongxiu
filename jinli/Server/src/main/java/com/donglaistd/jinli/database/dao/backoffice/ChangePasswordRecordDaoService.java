package com.donglaistd.jinli.database.dao.backoffice;

import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.backoffice.ChangePasswordRecord;
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

@Service
public class ChangePasswordRecordDaoService {
    private final ChangePasswordRecordRepository changePasswordRecordRepository;
    private final MongoTemplate mongoTemplate;
    private final UserDaoService userDaoService;

    public ChangePasswordRecordDaoService(ChangePasswordRecordRepository changePasswordRecordRepository, MongoTemplate mongoTemplate, UserDaoService userDaoService) {
        this.changePasswordRecordRepository = changePasswordRecordRepository;
        this.mongoTemplate = mongoTemplate;
        this.userDaoService = userDaoService;
    }

    public ChangePasswordRecord save(ChangePasswordRecord changePasswordRecord){
        return changePasswordRecordRepository.save(changePasswordRecord);
    }

    public PageInfo<ChangePasswordRecord> getChangePasswordRecordPageInfo(String userId, String displayName , PageRequest pageRequest) {
        List<String> userIds = new ArrayList<>();
        Query query = new Query();
        if(!StringUtils.isNullOrBlank(displayName)){
            User user = userDaoService.findByDisplayName(displayName);
            if(user == null) return new PageInfo<>(new ArrayList<>(), 0);
            userIds.add(user.getId());
        }
        if(!StringUtils.isNullOrBlank(userId)){
            userIds.add(userId);
        }
        if(!userIds.isEmpty()){
            query.addCriteria(Criteria.where("userId").in(userIds));
        }
        long totalNum = mongoTemplate.count(query, ChangePasswordRecord.class);
        List<ChangePasswordRecord> content = mongoTemplate.find(query.with(pageRequest).with(Sort.by(Sort.Direction.ASC, "time")), ChangePasswordRecord.class);
        return new PageInfo<>(content, totalNum);
    }
}
