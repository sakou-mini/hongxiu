package com.donglai.web.web.controller.server;

import com.donglai.common.constant.DomainArea;
import com.donglai.web.config.annotation.BackLog;
import com.donglai.web.process.H5DomainProcess;
import com.donglai.web.response.ErrorResponse;
import com.donglai.web.response.GlobalResponseCode;
import com.donglai.web.response.RestResponse;
import com.donglai.web.response.SuccessResponse;
import com.donglai.web.web.dto.request.H5DomainSetRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/domainConfig")
@Api(value = "H5DomainController", tags = "域名设置")
public class H5DomainController {
    @Autowired
    H5DomainProcess h5DomainProcess;

    @ApiOperation("h5域名列表")
    @GetMapping("/domainList")
    public RestResponse domainList() {
        return new SuccessResponse().withData(h5DomainProcess.getDomainList());
    }

    @ApiOperation("修改域名")
    @PostMapping("/updateDomain")
    @BackLog(name = "修改H5域名")
    public RestResponse updateDomainName(H5DomainSetRequest request){
        GlobalResponseCode globalResponseCode = h5DomainProcess.updateDomainName(request);
        return globalResponseCode.equals(GlobalResponseCode.SUCCESS) ? new SuccessResponse() : new ErrorResponse(globalResponseCode);
    }

    @ApiOperation("添加域名")
    @PostMapping("/addDomain")
    @BackLog(name = "添加H5域名")
    public RestResponse addDomain(@ApiParam(value = "域名") String domain, @ApiParam(value = "域名线路") DomainArea line){
        GlobalResponseCode globalResponseCode = h5DomainProcess.addH5Domain(domain, line);
        return globalResponseCode.equals(GlobalResponseCode.SUCCESS) ? new SuccessResponse() : new ErrorResponse(globalResponseCode);
    }

    @ApiOperation("删除域名")
    @PostMapping("/deleteDomain")
    @BackLog(name = "删除H5域名")
    public RestResponse addDomain(@ApiParam(value = "域名Id") long id){
        h5DomainProcess.deleteH5Domain(id);
        return new SuccessResponse();
    }
}
