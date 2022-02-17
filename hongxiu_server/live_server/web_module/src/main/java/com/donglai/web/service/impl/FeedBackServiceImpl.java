package com.donglai.web.service.impl;

import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.FeedBack;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.FeedBackService;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.service.BackOfficeUserService;
import com.donglai.web.db.server.service.FeedBackDbService;
import com.donglai.web.response.*;
import com.donglai.web.service.FeedBackBackService;
import com.donglai.web.util.ConvertUtils;
import com.donglai.web.web.dto.request.FeedbackDeleteRequest;
import com.donglai.web.web.dto.request.FeedbackFindListRequest;
import com.donglai.web.web.dto.request.FeedbackUpdateStatusRequest;
import com.donglai.web.web.vo.FeedBackVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author Moon
 * @date 2021-12-28 16:21
 */
@Service
public class FeedBackServiceImpl implements FeedBackBackService {

    @Autowired
    private UserService userService;
    @Autowired
    private FeedBackService feedBackService;
    @Autowired
    private BackOfficeUserService backOfficeUserService;
    @Autowired
    private FeedBackDbService feedBackDbService;

    @Override
    public RestResponse check(FeedbackUpdateStatusRequest request) {
        if (Objects.isNull(request.getId()) || Objects.isNull(request.getStatus())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        FeedBack feedBack = feedBackService.findById(request.getId());
        feedBack.setStatus(request.getStatus());
        return new SuccessResponse().withData(feedBackService.save(feedBack));
    }

    @Override
    public RestResponse reply(FeedbackUpdateStatusRequest request) {
        if (Objects.isNull(request.getId())
                || Objects.isNull(request.getStatus())
                || Objects.isNull(request.getReply())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        FeedBack feedBack = feedBackService.findById(request.getId());
        feedBack.setStatus(request.getStatus());
        feedBack.setReply(request.getReply());
        return new SuccessResponse().withData(feedBackService.save(feedBack));
    }

    @Override
    public RestResponse delete(FeedbackDeleteRequest request) {
        if (CollectionUtils.isEmpty(request.getIds())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        return new SuccessResponse().withData(feedBackService.deleteByIdIn(request.getIds()));
    }

    @Override
    public PageInfo<FeedBackVO> findList(FeedbackFindListRequest request) {

        PageInfo<FeedBack> feedBack = feedBackDbService.conditionGetFeedBack(request);
        List<FeedBackVO> feedBackVos = ConvertUtils.feedBackListToFeedBackVOList(feedBack.getContent());
        feedBackVos.forEach(v -> {
            String createdId = v.getCreatedId();
            if(StringUtils.isNotEmpty(createdId)){
                User byId = userService.findById(createdId);
                v.setCreatedId(byId.getNickname());
            }

        });

        return new PageInfo<>(feedBack.getPageNum(), feedBack.getPageSize(), feedBack.getTotal(), feedBackVos);

    }
}
