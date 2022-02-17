package com.donglai.web.db.server.service;

import com.donglai.common.util.CombineBeansUtil;
import com.donglai.model.db.entity.blogs.BlogsLabelsConfig;
import com.donglai.model.db.service.blogs.BlogsLabelsConfigService;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.response.*;
import com.donglai.web.web.dto.request.LabelListRequest;
import com.donglai.web.web.dto.request.UpdateLabelListStatusRequest;
import com.donglai.web.web.vo.LabelVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Moon
 * @date 2021-12-24 11:19
 */
@Service
public class LabelDbService {
    @Autowired
    private com.donglai.web.db.backoffice.service.BackOfficeUserService backOfficeUserService;
    @Autowired
    private BlogsLabelsConfigService blogsLabelsConfigService;
    @Autowired
    private com.donglai.model.db.service.blogs.BlogsLabelsService blogsLabelsService;
    @Autowired
    private MongoTemplate mongoTemplate;

    public PageInfo<LabelVO> conditionGetLabel(LabelListRequest request) {

        Criteria criteria = new Criteria();
        if (Objects.nonNull(request.getEnable())) {
            criteria.and("enable").is(request.getEnable());
        }
        if (Objects.nonNull(request.getLabelName())) {
            criteria.and("labelName").is(request.getLabelName());
        }
        if(Objects.nonNull(request.getLabelId())){
            criteria.and("id").is(request.getLabelId());
        }
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, BlogsLabelsConfig.class);
        List<BlogsLabelsConfig> labels = mongoTemplate.find(query.with(pageable).with(Sort.by(Sort.Direction.DESC, "createTime")), BlogsLabelsConfig.class);
        List<LabelVO> labelVOList = labels.stream().map(label -> new CombineBeansUtil<>(LabelVO.class).combineBeans(label)).collect(Collectors.toList());
        for (LabelVO labelVO : labelVOList) {
            //使用次数
            labelVO.setUseCount(blogsLabelsService.findByLabelsId(labelVO.getId()).size());
            //操作人名字
            String backstageUserId = labelVO.getBackstageUserId();
            if(StringUtils.isNotEmpty(backstageUserId)){
                BackOfficeUser byId = backOfficeUserService.findById(backstageUserId);
                labelVO.setBackstageUserId(byId.getNickname());
            }
        }

        return new PageInfo<>(pageable, labelVOList, count);
    }

    public RestResponse addLabel(String labelName) {
        if (Objects.nonNull(blogsLabelsConfigService.findByLabelName(labelName))) {
            return new ErrorResponse(GlobalResponseCode.LABEL_ERROR);
        }
        BackOfficeUser principal =(BackOfficeUser) SecurityUtils.getSubject().getPrincipal();
        BlogsLabelsConfig blogsLabelsConfig = new BlogsLabelsConfig();
        blogsLabelsConfig.setLabelName(labelName);
        blogsLabelsConfig.setBackstageUserId(principal.getId());
        blogsLabelsConfig.setCreateTime(System.currentTimeMillis());
        blogsLabelsConfig.setUpdateTime(System.currentTimeMillis());
        return new SuccessResponse().withData(blogsLabelsConfigService.save(blogsLabelsConfig));
    }

    public List<BlogsLabelsConfig> updateStatusList(UpdateLabelListStatusRequest request) {
        List<BlogsLabelsConfig> byLabelsIdIn = blogsLabelsConfigService.findByLabelsIdIn(request.getLabels());
        BackOfficeUser principal =(BackOfficeUser) SecurityUtils.getSubject().getPrincipal();
        byLabelsIdIn.forEach(v -> {
            v.setEnable(request.getStatus());
            v.setUpdateTime(System.currentTimeMillis());
            v.setBackstageUserId(principal.getId());
        });
        blogsLabelsConfigService.saveAll(byLabelsIdIn);
        return byLabelsIdIn;
    }

    public List<BlogsLabelsConfig> delLabelList(UpdateLabelListStatusRequest request) {
        return blogsLabelsConfigService.delListIn(request.getLabels());
    }

    public RestResponse update(LabelListRequest request) {
        if (Objects.isNull(request.getLabelId())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        BackOfficeUser principal = (BackOfficeUser) SecurityUtils.getSubject().getPrincipal();
        BlogsLabelsConfig byId = blogsLabelsConfigService.findId(request.getLabelId());
        if (Objects.nonNull(byId)) {
            byId.setBackstageUserId(principal.getId());
            byId.setUpdateTime(System.currentTimeMillis());
            if (Objects.nonNull(request.getEnable())) {
                byId.setEnable(request.getEnable());
            }
            if (Objects.nonNull(request.getLabelName())) {
                byId.setLabelName(request.getLabelName());
            }
            return new SuccessResponse().withData(blogsLabelsConfigService.save(byId));
        }
        return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
    }

    public RestResponse findAll() {
        return new SuccessResponse().withData(blogsLabelsConfigService.findAll());
    }
}
