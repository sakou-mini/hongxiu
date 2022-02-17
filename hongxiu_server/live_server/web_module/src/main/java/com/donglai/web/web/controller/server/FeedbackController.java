package com.donglai.web.web.controller.server;

import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.service.FeedBackBackService;
import com.donglai.web.web.dto.request.FeedbackDeleteRequest;
import com.donglai.web.web.dto.request.FeedbackFindListRequest;
import com.donglai.web.web.dto.request.FeedbackUpdateStatusRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Moon
 * @date 2021-12-28 16:14
 */
@Api(tags = "反馈建议管理")
@RestController
@RequestMapping("/api/v1/feedback")
public class FeedbackController {

    @Autowired
    private FeedBackBackService feedBackService;

    @ApiOperation(value = "获取反馈列表")
    @PostMapping("/findList")
    public RestResponse findList(FeedbackFindListRequest request) {
        return new SuccessResponse().withData(feedBackService.findList(request));
    }

    @ApiOperation(value = "反馈查看")
    @PostMapping("/check")
    public RestResponse check(FeedbackUpdateStatusRequest request) {
        return feedBackService.check(request);
    }

    @ApiOperation(value = "反馈回复")
    @PostMapping("/reply")
    public RestResponse reply(FeedbackUpdateStatusRequest request) {
        return feedBackService.reply(request);
    }


    @ApiOperation(value = "反馈删除")
    @PostMapping("/delete")
    public RestResponse delete(FeedbackDeleteRequest request) {
        return feedBackService.delete(request);
    }
}



