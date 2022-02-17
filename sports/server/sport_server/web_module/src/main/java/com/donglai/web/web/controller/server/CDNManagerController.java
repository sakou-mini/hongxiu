package com.donglai.web.web.controller.server;

import com.donglai.web.config.annotation.BackLog;
import com.donglai.web.process.CDNProcess;
import com.donglai.web.response.ErrorResponse;
import com.donglai.web.response.GlobalResponseCode;
import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.web.dto.request.UpdateLiveDomainRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cdn")
@Api(value = "CDNManagerController", tags = "CDN管理")
public class CDNManagerController {
    @Autowired
    CDNProcess cdnProcess;

    @ApiOperation("CDN列表")
    @GetMapping("/cdnList")
    public RestResponse cdnList(){
        return new SuccessResponse().withData(cdnProcess.getLiveDomainList());
    }

    @ApiOperation("启用/关闭CDN")
    @PostMapping("/enableOrClose")
    @BackLog(name = "启用/关闭CDN")
    public RestResponse enableCDN(@ApiParam(value = "线路id", example = "0") int lineCode,
                                  @ApiParam(value = "状态 true 启用、false 禁用", example = "true") boolean status){
        GlobalResponseCode globalResponseCode = cdnProcess.enableOrCloseCNDLine(lineCode, status);
        return globalResponseCode.equals(GlobalResponseCode.SUCCESS) ? new SuccessResponse() : new ErrorResponse(globalResponseCode);
    }

    @ApiOperation("CDN域名修改")
    @PostMapping("/liveDomainConfig")
    @BackLog(name = "CDN域名修改")
    public RestResponse enableCDN(UpdateLiveDomainRequest request){
        GlobalResponseCode globalResponseCode = cdnProcess.updateLiveDomain(request);
        return globalResponseCode.equals(GlobalResponseCode.SUCCESS) ? new SuccessResponse() : new ErrorResponse(globalResponseCode);
    }
}


