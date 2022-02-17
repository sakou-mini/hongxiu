package com.donglai.account.db.service;

import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserTotalService {
    @Autowired
    UserService userService;
    @Autowired
    MongoTemplate mongoTemplate;

    //时间段内的新增用户数量 [start,end)
    public long countNewUserNumByTimeBetween(long startTime, long endTime) {
        Criteria timeCriteria = Criteria.where("createTime").gte(startTime).lt(endTime);
        return mongoTemplate.count(Query.query(timeCriteria), User.class);
    }

    //老用户数量（注册日期小起始时间且登陆过的)
    public long countOldUserNumByTime(long time) {
        Criteria timeCriteria = Criteria.where("createTime").lte(time).and("lastLoginTime").gt(0);
        return mongoTemplate.count(Query.query(timeCriteria), User.class);
    }

    public List<User> queryLoginUserNumByTimeBetween(long startTime, long endTime) {
        Criteria loginTimeCriteria = Criteria.where("lastLoginTime").gte(startTime).lt(endTime);
        return mongoTemplate.find(Query.query(loginTimeCriteria), User.class);
    }
}

