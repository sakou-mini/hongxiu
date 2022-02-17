package com.donglai.web.web.controller.server;

import com.donglai.model.db.entity.live.Banner;
import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.service.BannerBackService;
import com.donglai.web.web.dto.request.BannerFindListRequest;
import com.donglai.web.web.dto.request.BannerUpdateStatusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Moon
 * @date 2022-01-04 16:09
 */
@RestController
@RequestMapping("/api/v1/banner")
public class BannerController {

    @Autowired
    private BannerBackService bannerBackService;


    @PostMapping("/findList")
    public RestResponse findList(BannerFindListRequest request) {
        return new SuccessResponse().withData(bannerBackService.findList(request));
    }

    @PostMapping("/updateStatus")
    public RestResponse updateStatus(BannerUpdateStatusRequest request) {
        return bannerBackService.updateStatus(request);
    }

    @PostMapping("/delete")
    public RestResponse delete(BannerUpdateStatusRequest request) {
        return bannerBackService.delete(request);
    }

    @PostMapping("/insert")
    public RestResponse insert(Banner request) {
        return bannerBackService.insert(request);
    }

    @PostMapping("/update")
    public RestResponse update(Banner request) {
        return bannerBackService.update(request);
    }


}
