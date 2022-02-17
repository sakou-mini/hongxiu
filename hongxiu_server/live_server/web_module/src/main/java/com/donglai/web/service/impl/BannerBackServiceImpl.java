package com.donglai.web.service.impl;

import com.donglai.common.constant.BannerStatueEnum;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.live.Banner;
import com.donglai.model.db.service.live.BannerService;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.service.BackOfficeUserService;
import com.donglai.web.db.server.service.BannerDbService;
import com.donglai.web.response.*;
import com.donglai.web.service.BannerBackService;
import com.donglai.web.web.dto.request.BannerFindListRequest;
import com.donglai.web.web.dto.request.BannerUpdateStatusRequest;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author Moon
 * @date 2022-01-04 16:22
 */
@Service
public class BannerBackServiceImpl implements BannerBackService {

    @Autowired
    private BackOfficeUserService backOfficeUserService;
    @Autowired
    private BannerService bannerService;
    @Autowired
    private BannerDbService bannerDbService;

    @Override
    public PageInfo<Banner> findList(BannerFindListRequest request) {
        PageInfo<Banner> bannerPageInfo = bannerDbService.conditionGetBanner(request);

        for (Banner banner : bannerPageInfo.getContent()) {
            if (!StringUtils.isNullOrBlank(banner.getCreatedId())) {
                BackOfficeUser byId = backOfficeUserService.findById(banner.getCreatedId());
                banner.setCreatedId(byId.getNickname());
            }
        }
        return bannerPageInfo;
    }

    @Override
    public RestResponse update(Banner request) {


        if (Objects.isNull(request.getBannerUrl())
                || Objects.isNull(request.getTitle())
                || request.getStartTime() == 0
                || request.getEndTime() == 0) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        if (request.getStartTime() >= System.currentTimeMillis()) {
            request.setStatus(BannerStatueEnum.NOT_START.getValue());
        } else if (request.getStartTime() < System.currentTimeMillis() && System.currentTimeMillis() <= request.getEndTime()) {
            request.setStatus(BannerStatueEnum.RUNNING.getValue());
        } else if (request.getEndTime() < System.currentTimeMillis()) {
            request.setStatus(BannerStatueEnum.OVER.getValue());
        }


        return new SuccessResponse().withData(bannerService.save(request));
    }

    @Override
    public RestResponse insert(Banner request) {
        if (Objects.isNull(request.getBannerUrl())
                || Objects.isNull(request.getTitle())
                || request.getStartTime() == 0
                || request.getEndTime() == 0) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        if (request.getStartTime() >= System.currentTimeMillis()) {
            request.setStatus(BannerStatueEnum.NOT_START.getValue());
        } else if (request.getStartTime() < System.currentTimeMillis() && System.currentTimeMillis() <= request.getEndTime()) {
            request.setStatus(BannerStatueEnum.RUNNING.getValue());
        } else if (request.getEndTime() < System.currentTimeMillis()) {
            request.setStatus(BannerStatueEnum.OVER.getValue());
        }
        BackOfficeUser principal =(BackOfficeUser) SecurityUtils.getSubject().getPrincipal();
        request.setCreatedId(principal.getId());
        return new SuccessResponse().withData(bannerService.save(request));
    }

    @Override
    public RestResponse delete(BannerUpdateStatusRequest request) {

        if (CollectionUtils.isEmpty(request.getIds())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        return new SuccessResponse().withData(bannerService.deleteByIdIn(request.getIds()));
    }

    @Override
    public RestResponse updateStatus(BannerUpdateStatusRequest request) {
        if (CollectionUtils.isEmpty(request.getIds()) || Objects.isNull(request.getStatus())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        BackOfficeUser principal =(BackOfficeUser) SecurityUtils.getSubject().getPrincipal();


        List<Banner> byIdIn = bannerService.findByIdIn(request.getIds());
        //暂停
        if (request.getStatus().equals(3)) {
            byIdIn.forEach(v -> v.setStatus(request.getStatus()));
        } else {
            byIdIn.forEach(v -> {
                if (v.getStartTime() >= System.currentTimeMillis()) {
                    v.setStatus(BannerStatueEnum.NOT_START.getValue());
                } else if (v.getStartTime() < System.currentTimeMillis() && System.currentTimeMillis() <= v.getEndTime()) {
                    v.setStatus(BannerStatueEnum.RUNNING.getValue());
                } else if (v.getEndTime() < System.currentTimeMillis()) {
                    v.setStatus(BannerStatueEnum.OVER.getValue());
                }

            });
        }

        return new SuccessResponse().withData(bannerService.saveAll(byIdIn));
    }
}
