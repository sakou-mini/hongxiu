package com.donglai.web.db.server.service;

import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.blogs.AuditEnum;
import com.donglai.model.db.entity.blogs.BlogsComment;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Constant;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.BlogsOrCommentListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.donglai.web.constant.DataBaseFiledConstant.*;
import static com.donglai.web.db.server.service.CommonQueryService.getCriteriaByTimes;

@Service
public class BlogsCommentQueryService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    UserService userService;

    public PageInfo<BlogsComment> queryCommentListByPage(BlogsOrCommentListRequest request) {
        Criteria criteria = new Criteria();
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), Sort.by(Sort.Direction.ASC, COMMENT_COMMENT_TIME));
        List<String> userIdList = new ArrayList<>();
        var criteriaList = new ArrayList<Criteria>();
        //根据昵称查询动态
        if (!StringUtils.isNullOrBlank(request.getPublishName())) {
            List<String> userIds = userService.findByNickname(request.getPublishName()).stream().map(User::getId).collect(Collectors.toList());
            if (userIds.isEmpty()) return new PageInfo<>(request.getPage(), request.getSize(), 0L, new ArrayList<>());
            userIdList.addAll(userIds);
        }
        if (!StringUtils.isNullOrBlank(request.getUserId())) userIdList.add(request.getUserId());
        if (!userIdList.isEmpty())
            criteriaList.add(Criteria.where(BLOG_USER_ID).in(userIdList));
        //根据状态查询
        if (request.getStatus() >= 0) {
            criteriaList.add(Criteria.where(COMMENT_STATUS).is(Constant.CommentStatus.forNumber(request.getStatus())));
        }
        //根据机审核状态查询
        if (request.getSystemAuditStatus() >= 0)
            criteriaList.add(Criteria.where(COMMENT_SYSTEM_AUDIT_STATUS).is(AuditEnum.forNumber(request.getSystemAuditStatus())));
        //根据时间段查询
        criteria = getCriteriaByTimes(request.getStartTime(), request.getEndTime(), criteria, criteriaList, COMMENT_COMMENT_TIME);
        Query query = Query.query(criteria);
        long totalNum = mongoTemplate.count(query, BlogsComment.class);
        List<BlogsComment> blogsComments = mongoTemplate.find(query.with(pageable), BlogsComment.class);
        return new PageInfo<>(pageable, blogsComments, totalNum);
    }

}
