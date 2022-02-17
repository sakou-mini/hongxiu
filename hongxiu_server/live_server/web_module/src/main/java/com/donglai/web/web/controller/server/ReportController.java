package com.donglai.web.web.controller.server;

import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.service.ReportService;
import com.donglai.web.web.dto.request.ReportFindListRequest;
import com.donglai.web.web.dto.request.ReportUpdateHandelRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Moon
 * @date 2021-12-27 13:44
 */
@RestController
@RequestMapping("/api/v1/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/findVideoList")
    public RestResponse findVideoList(ReportFindListRequest request) {
        return new SuccessResponse().withData(reportService.findVideoList(request));
    }

    @PostMapping("/updateVideoHandel")
    public RestResponse updateVideoHandel(ReportUpdateHandelRequest request) {
        return reportService.updateVideoHandel(request);
    }

    @PostMapping("/findCommentList")
    public RestResponse findCommentList(ReportFindListRequest request) {
        return new SuccessResponse().withData(reportService.findCommentList(request));
    }

    @PostMapping("/updateCommentHandel")
    public RestResponse updateCommentHandel(ReportUpdateHandelRequest request) {
        return reportService.updateCommentHandel(request);
    }

    @PostMapping("/findUserList")
    public RestResponse findUserList(ReportFindListRequest request) {
        return new SuccessResponse().withData(reportService.findUserList(request));
    }

    @PostMapping("/updateUserHandel")
    public RestResponse updateUserHandel(ReportUpdateHandelRequest request) {
        return reportService.updateUserHandel(request);
    }
}
