package com.donglai.statistics.db.service;

import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserTotalService {
    @Autowired
    UserService userService;
    @Autowired
    MongoTemplate mongoTemplate;

    //时间段内的新增用户数量（是否游客） [start,end)
    public long countNewUserNumByTimeBetween(long startTime, long endTime, boolean isTourist) {
        Criteria timeCriteria = Criteria.where("createTime").gte(startTime).lt(endTime).and("tourist").is(isTourist);
        return mongoTemplate.count(Query.query(timeCriteria), User.class);
    }

    //老用户数量（非游客，且注册日期小起始时间） [start,end)
    public long countOldUserNumByTime(long time) {
        return userService.countOldUserNumByTime(time);
    }

    //查询时间段内登录的用户ip记录
    public Set<String> queryLoginIpListByTimeBetween(long startTime, long endTime) {
        Criteria loginTimeCriteria = Criteria.where("lastLoginTime").gte(startTime).lt(endTime);
        List<User> users = mongoTemplate.find(Query.query(loginTimeCriteria), User.class);
        return users.stream().filter(user -> !StringUtils.isNullOrBlank(user.getLastLoginIp())).map(User::getLastLoginIp).collect(Collectors.toSet());
    }

}

