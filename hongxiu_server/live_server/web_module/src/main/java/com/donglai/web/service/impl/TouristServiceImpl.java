package com.donglai.web.service.impl;

import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.statistics.DailyOfServerData;
import com.donglai.model.db.service.blogs.BlogsLikeService;
import com.donglai.model.db.service.live.TouristLoginLogService;
import com.donglai.web.db.server.service.CommonQueryService;
import com.donglai.web.db.server.service.TouristDbService;
import com.donglai.web.response.PageInfo;
import com.donglai.web.service.TouristService;
import com.donglai.web.util.ConvertUtils;
import com.donglai.web.web.dto.request.TouristListRequest;
import com.donglai.web.web.vo.TouristVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-22 10:39
 */
@Service
public class TouristServiceImpl implements TouristService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private TouristLoginLogService touristLoginLogService;
    @Autowired
    private BlogsLikeService blogsLikeService;
    @Autowired
    private TouristDbService touristDbService;


    @Override
    public PageInfo<DailyOfServerData> touristStatisticsList(TouristListRequest request) {
        Criteria criteria = CommonQueryService.getCriteriaByTimes(request.getTouristStatisticsTimeStart(),
                request.getTouristStatisticsTimeEnd(), new Criteria(), "recordTime");
        Pageable pageable = PageRequest.of(request.getPage()-1, request.getSize());
        Query query = Query.query(criteria);
        List<DailyOfServerData> users = mongoTemplate.find(query.with(pageable), DailyOfServerData.class);
        long count = mongoTemplate.count(query, DailyOfServerData.class);
        return new PageInfo<>(pageable, users, count);
    }

    @Override
    public PageInfo<TouristVO> touristList(TouristListRequest request) {
        PageInfo<User> userPageInfo = touristDbService.conditionGetTourist(request);
        List<TouristVO> tourists = ConvertUtils.userListToTouristVOList(userPageInfo.getContent());
        for (TouristVO touristVO : tourists) {
            //登录次数
            touristVO.setLoginCount(touristLoginLogService.findByUserIdLoginCount(touristVO.getId()));
            //喜欢次数
            touristVO.setLikeCount(blogsLikeService.countLikeBlogsByUserId(touristVO.getId()));
        }
        return new PageInfo<>(userPageInfo.getPageNum(), userPageInfo.getPageSize(), userPageInfo.getTotal(), tourists);
    }
}
