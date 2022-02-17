package com.donglai.web.db.server.service;

import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.blogs.AuditEnum;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.repository.blogs.BlogsRepository;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Constant;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.BlogsOrCommentListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.donglai.web.constant.DataBaseFiledConstant.*;
import static com.donglai.web.db.server.service.CommonQueryService.getCriteriaByTimes;

@Service
public class BlogsQueryService {
    @Autowired
    BlogsRepository blogsRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UserService userService;

    public PageInfo<Blogs> queryBlogsByBlogsListRequest(BlogsOrCommentListRequest request) {
        Criteria criteria = new Criteria();
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), Sort.by(Sort.Direction.ASC, BLOG_ID));
        LookupOperation blogsReview = LookupOperation.newLookup().from("blogsReview").localField("_id").foreignField("_id").as("blogsReview");
        var criteriaList = new ArrayList<Criteria>();
        List<String> userIdList = new ArrayList<>();
        //根据昵称查询动态
        if (!StringUtils.isNullOrBlank(request.getPublishName())) {
            List<String> userIds = userService.findByNickname(request.getPublishName()).stream().map(User::getId).collect(Collectors.toList());
            if (userIds.isEmpty()) return new PageInfo<>(request.getPage(), request.getSize(), 0L, new ArrayList<>());
            userIdList.addAll(userIds);
        }
        if (request.getStatus() >= 0) {
            criteriaList.add(Criteria.where(BLOG_STATUS).is(Constant.BlogsStatus.forNumber(request.getStatus())));
        }
        //根据用户id查询
        if (!StringUtils.isNullOrBlank(request.getUserId()))
            userIdList.add(request.getUserId());
        if (!userIdList.isEmpty())
            criteriaList.add(Criteria.where(BLOG_USER_ID).in(userIdList));
        //根据机审核状态查询
        if (request.getSystemAuditStatus() >= 0)
            criteriaList.add(Criteria.where("blogsReview.systemAuditStatus").is(AuditEnum.forNumber(request.getSystemAuditStatus())));
        //根据起始时间
        criteria = getCriteriaByTimes(request.getStartTime(), request.getEndTime(), criteria, criteriaList, BLOG_CREATE_AT);
        return commonPageQuery(pageable, criteria, blogsReview);
    }

    private PageInfo<Blogs> commonPageQuery(Pageable pageInfo, Criteria criteria, LookupOperation lookupOperation) {
        int totalNum = mongoTemplate.aggregate(Aggregation.newAggregation(lookupOperation, Aggregation.match(criteria)), Blogs.class, Blogs.class).getMappedResults().size();
        int page = pageInfo.getPageNumber();
        int size = pageInfo.getPageSize();
        Aggregation agg = Aggregation.newAggregation(lookupOperation, Aggregation.match(criteria), Aggregation.sort(pageInfo.getSort()), Aggregation.skip(page > 1 ? (page - 1) * size : 0L),
                Aggregation.limit(size));
        return new PageInfo<>(pageInfo, mongoTemplate.aggregate(agg, Blogs.class, Blogs.class).getMappedResults(), totalNum);
    }
}
