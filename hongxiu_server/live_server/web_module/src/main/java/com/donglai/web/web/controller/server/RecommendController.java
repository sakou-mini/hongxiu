package com.donglai.web.web.controller.server;

import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.service.RecommendBackService;
import com.donglai.web.web.dto.request.RecommendFindUserListRequest;
import com.donglai.web.web.dto.request.RecommendFindVideoListRequest;
import com.donglai.web.web.dto.request.RecommendUpdateStatusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Moon
 * @date 2022-01-05 11:25
 */
@RestController
@RequestMapping("/api/v1/recommend")
public class RecommendController {

    @Autowired
    private RecommendBackService recommendBackService;

    @PostMapping("/findVideoList")
    public RestResponse findVideoList(RecommendFindVideoListRequest request) {
        return new SuccessResponse().withData(recommendBackService.findVideoList(request));
    }

    @PostMapping("/updateVideoStatus")
    public RestResponse updateVideoStatus(RecommendUpdateStatusRequest request) {
        return recommendBackService.updateVideoStatus(request);
    }

    @PostMapping("/deletedVideo")
    public RestResponse deletedVideo(RecommendUpdateStatusRequest request) {
        return recommendBackService.deletedVideo(request);
    }

    @PostMapping("/addVideo")
    public RestResponse addVideo(Long blogId) {
        return recommendBackService.addVideo(blogId);
    }


    @PostMapping("/findUserList")
    public RestResponse findUserList(RecommendFindUserListRequest request) {
        return new SuccessResponse().withData(recommendBackService.findUserList(request));
    }

    @PostMapping("/updateUserStatus")
    public RestResponse updateUserStatus(RecommendUpdateStatusRequest request) {
        return recommendBackService.updateUserStatus(request);
    }

    @PostMapping("/deletedUser")
    public RestResponse deletedUser(RecommendUpdateStatusRequest request) {
        return recommendBackService.deletedUser(request);
    }

    @PostMapping("/addUser")
    public RestResponse addUser(String userId, String log) {
        return recommendBackService.addUser(userId, log);
    }

}
