package com.donglaistd.jinli.service.statistic;

import com.donglaistd.jinli.database.dao.FeedbackDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.Feedback;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.http.entity.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ServicePageProcess {
    @Autowired
    FeedbackDaoService feedbackDaoService;
    @Autowired
    UserDaoService userDaoService;

    public PageInfo<Map<String,Object>> getFeedbackPageInfo(Long startTime, Long endTime , String keyWords, int page, int size){
        PageInfo<Feedback> pageInfo = feedbackDaoService.findFeedbackPageInfo(startTime, endTime, keyWords, PageRequest.of(page, size));
        List<Map<String, Object>> content = new ArrayList<>();
        User user;
        JSONObject jsonObject;
        for (Feedback feedback : pageInfo.getContent()) {
            user = userDaoService.findById(feedback.getUserId());
            if(user == null) continue;
            jsonObject = new JSONObject(feedback);
            jsonObject.put("displayName", user.getDisplayName());
            content.add(jsonObject.toMap());
        }
        return new PageInfo<>(content, pageInfo.getTotal());
    }
}
